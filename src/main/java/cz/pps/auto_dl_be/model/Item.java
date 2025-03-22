package cz.pps.auto_dl_be.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item extends AbstractEntity {
    private String productCode;
    private String manufacturer;
    private String productName;
    private String mainStock;
    private String otherBranchStock;
    private String supplierStock;
    private String price;
    private String vatRate;
    private String currency;
    private String deposit;
    private String tecDocId;
    private String tecDocSupplierName;
    private String tecDocSupplierID = null;

    @Override
    public String toString() {
        return "Item{\n" +
                "productCode='" + productCode + '\'' + "\n" +
                "manufacturer='" + manufacturer + '\'' + "\n" +
                "productName='" + productName + '\'' + "\n" +
                "mainStock='" + mainStock + '\'' + "\n" +
                "otherBranchStock='" + otherBranchStock + '\'' + "\n" +
                "supplierStock='" + supplierStock + '\'' + "\n" +
                "price='" + price + '\'' + "\n" +
                "vatRate='" + vatRate + '\'' + "\n" +
                "currency='" + currency + '\'' + "\n" +
                "deposit='" + deposit + '\'' + "\n" +
                "tecDocld='" + tecDocId + '\'' + "\n" +
                "tecDocSupplierName='" + tecDocSupplierName + '\'' + "\n" +
                "tecDocSupplierID='" + tecDocSupplierID + '\'' + "\n" +
                '}';
    }
}