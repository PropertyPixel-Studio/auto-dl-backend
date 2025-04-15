# TecDoc API volání pro Značky, Modely a Typy Vozidel

Tento dokument popisuje, jak se pomocí TecDoc SOAP API získávají informace o značkách (výrobcích), modelech a typech (motorizacích) vozidel. Primárně se využívá SOAP akce `getLinkageTargets`.

## Základní princip

Používá se jedno hlavní volání (`getLinkageTargets`), které se postupně filtruje pomocí ID získaných z předchozích kroků k získání detailnějších informací v hierarchii: Značka -> Model -> Typ.

## Kroky volání

Zde jsou příklady parametrů a ukázky kódu pro jednotlivé kroky volání `getLinkageTargets` (založeno na TypeScript implementaci v `src/tecdoc/requests/manufacturers.ts` a `src/tecdoc/utils/soapBodyTemplate.ts`).

### Sestavení SOAP Požadavku (TypeScript Příklad)

Nejprve ukázka pomocné funkce `createSoapRequest`, která generuje základní SOAP XML strukturu:

```typescript
// src/tecdoc/utils/soapBodyTemplate.ts
export function createSoapRequest(
  method: string,
  params: Record<string, any>
): string {
  const paramXml = Object.entries(params)
    .map(([key, value]) => `         <tec:${key}>${value}</tec:${key}>`)
    .join("\n")

  return `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tec="http://www.tecalliance.services/pegasus-3-0">
   <soapenv:Header/>
   <soapenv:Body>
      <tec:${method}>
${paramXml}
      </tec:${method}>
   </soapenv:Body>
</soapenv:Envelope>`
}
```

Tato funkce je volána funkcí `createGetLinkageTargetsRequest` pro sestavení konkrétního požadavku:

```typescript
// src/tecdoc/requests/manufacturers.ts
import { createSoapRequest } from "../utils/soapBodyTemplate"
// ... další importy

export function createGetLinkageTargetsRequest(
  params: GetLinkageTargetsRequest // Typ definující možné parametry
): string {
  const requestParams: Record<string, any> = {
    linkageTargetCountry: params.linkageTargetCountry,
    lang: params.lang.toUpperCase(),
    provider: "${provider}", // Nahradit skutečným ID poskytovatele
    linkageTargetType: params.linkageTargetType || "V",
    includeMfrFacets: params.includeMfrFacets ?? true,
    perPage: params.perPage ?? 0,
    page: params.page ?? 1,
    filterMode: params.filterMode || "preferred",
  }

  // Přidání volitelných parametrů pro filtrování modelů a typů
  if (params.mfrIds) {
    requestParams.mfrIds = params.mfrIds
  }
  if (params.vehicleModelSeriesIds) {
    requestParams.vehicleModelSeriesIds = params.vehicleModelSeriesIds
  }
  if (params.includeVehicleModelSeriesFacets) {
    requestParams.includeVehicleModelSeriesFacets = params.includeVehicleModelSeriesFacets
  }

  // Volání pomocné funkce pro vytvoření SOAP těla
  return createSoapRequest("getLinkageTargets", requestParams)
}

```

### 1. Získání značek (Manufacturers)

Cílem je získat seznam všech dostupných značek vozidel pro danou zemi a jazyk.

**Příklad volání `createGetLinkageTargetsRequest`:**

```typescript
const brandParams = {
  linkageTargetCountry: "CZ", // Kód země (např. Česká republika)
  lang: "CS",                 // Kód jazyka (např. Čeština)
  linkageTargetType: "V",     // Typ cíle = Vozidla (Vehicle)
  includeMfrFacets: true      // Požadujeme seznam značek v odpovědi
  // provider: "TECDOC_PROVIDER_ID" // ID poskytovatele API klíče
  // perPage, page: pro stránkování, pokud je potřeba
};
```

**Očekávaná odpověď:** Odpověď bude obsahovat element `mfrFacets`, ve kterém bude seznam značek s jejich ID (`mfrId`) a názvem (`mfrName`). Příklad parsování odpovědi viz níže.

### 2. Získání modelů (Model Series) pro konkrétní značku

Cílem je získat seznam modelových řad pro specifickou značku, jejíž ID bylo získáno v kroku 1.

**Příklad volání `createGetLinkageTargetsRequest`:**

```typescript
// Předpokládejme ID značky Škoda = 74 (získané z kroku 1)
const manufacturerId = 74; 

const modelParams = {
  linkageTargetCountry: "CZ",
  lang: "CS",
  linkageTargetType: "V",
  mfrIds: [manufacturerId], // Filtrujeme podle ID značky
  includeVehicleModelSeriesFacets: true // Požadujeme seznam modelů v odpovědi
  // provider: "TECDOC_PROVIDER_ID"
};
```

**Očekávaná odpověď:** Odpověď bude obsahovat element `vehicleModelSeriesFacets`, ve kterém bude seznam modelových řad pro danou značku (např. Škoda) s jejich ID (`vehicleModelSeriesId`) a názvem. Příklad parsování odpovědi viz níže.

### 3. Získání typů/motorizací (Linkage Targets) pro konkrétní model

Cílem je získat konkrétní typy vozidel (motorizace, karoserie, roky výroby) pro specifickou modelovou řadu, jejíž ID bylo získáno v kroku 2.

**Příklad volání `createGetLinkageTargetsRequest`:**

```typescript
// Předpokládejme ID modelu Octavia = 2084 (získané z kroku 2)
const modelSeriesId = 2084;

const typeParams = {
  linkageTargetCountry: "CZ",
  lang: "CS",
  linkageTargetType: "V",
  vehicleModelSeriesIds: [modelSeriesId], // Filtrujeme podle ID modelu
  // provider: "TECDOC_PROVIDER_ID"
};
```

**Očekávaná odpověď:** Odpověď bude obsahovat element `linkageTargets`, což je pole objektů reprezentujících jednotlivé typy vozidel (motorizace) pro daný model. Každý objekt obsahuje detaily jako `carId`, `carName`, rok výroby, výkon atd. Příklad parsování odpovědi viz níže.

### Zpracování (Parsování) SOAP Odpovědi (TypeScript Příklad)

Funkce `parseGetLinkageTargetsResponse` ukazuje, jak extrahovat data z naparsované SOAP odpovědi (předpokládá se, že vstup `parsedData` je již převedený XML na JavaScript objekt, např. pomocí knihovny jako `xml2js`):

```typescript
// src/tecdoc/requests/manufacturers.ts
import { GetLinkageTargetsResponse } from "../types/manufacturers" // Typ pro odpověď
// ...

export function parseGetLinkageTargetsResponse(
  parsedData: any // Objekt vzniklý parsováním XML odpovědi
): GetLinkageTargetsResponse {
  // Navigace přes strukturu SOAP odpovědi k relevantním datům
  const responseBody = parsedData?.["S:Envelope"]?.["S:Body"]?.[0]?.[
    "ns2:getLinkageTargetsResponse"
  ]?.[0];

  // Extrakce seznamu značek (pokud jsou v odpovědi)
  const manufacturers = responseBody?.["mfrFacets"]?.[0]?.["counts"] || [];
  
  // Extrakce seznamu modelů (pokud jsou v odpovědi)
  const modelSeries = responseBody?.["vehicleModelSeriesFacets"]?.[0]?.["counts"] || [];
  
  // Extrakce seznamu typů vozidel (pokud jsou v odpovědi)
  const linkageTargets = responseBody?.["linkageTargets"] || [];

  // Vrácení strukturovaného objektu s extrahovanými daty
  return { 
    manufacturers, // Seznam značek (relevantní pro krok 1)
    modelSeries: modelSeries.length > 0 ? modelSeries : undefined, // Seznam modelů (relevantní pro krok 2)
    linkageTargets: linkageTargets.length > 0 ? linkageTargets : undefined // Seznam typů (relevantní pro krok 3)
  };
}
```

## Poznámky pro implementaci (např. v Java/Spring)

*   **SOAP Request:** Je potřeba sestavit validní SOAP XML požadavek pro akci `getLinkageTargets`, který bude obsahovat výše uvedené parametry ve správné struktuře. V Javě lze použít knihovny jako JAX-WS.
*   **Provider ID:** Hodnota `provider` (v příkladech jako `TECDOC_PROVIDER_ID`) je unikátní identifikátor vašeho API klíče od TecDoc.
*   **SOAP Response Parsing:** Odpověď je také ve formátu SOAP XML. Je nutné ji naparsovat (např. pomocí JAXB v Javě) a extrahovat relevantní data z elementů `mfrFacets`, `vehicleModelSeriesFacets` nebo `linkageTargets` v závislosti na kroku volání. Funkce `parseGetLinkageTargetsResponse` ukazuje princip extrakce.
*   **Chybové stavy:** Je třeba ošetřit možné chyby API (neplatné parametry, problémy s autentizací atd.).

Tento postup s konkrétnějšími ukázkami kódu by měl poskytnout jasnější návod pro implementaci získávání dat o vozidlech z TecDoc API.
