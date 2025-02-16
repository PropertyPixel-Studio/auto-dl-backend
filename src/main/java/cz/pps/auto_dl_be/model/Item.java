package cz.pps.auto_dl_be.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
//@Table(name = "item", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item extends AbstractEntity {
//    @Column(name = "productCode", nullable = true)
    private String productCode;

//    @Column(name = "manufacturer", nullable = true)
    private String manufacturer;

//    @Column(name = "productName", nullable = true)
    private String productName;

//    @Column(name = "mainStock", nullable = true)
    private String mainStock;

//    @Column(name = "otherBranchStock", nullable = true)
    private String otherBranchStock;

//    @Column(name = "supplierStock", nullable = true)
    private String supplierStock;

//    @Column(name = "price", nullable = true)
    private String price;

//    @Column(name = "vatRate", nullable = true)
    private String vatRate;

//    @Column(name = "currency", nullable = true)
    private String currency;

//    @Column(name = "deposit", nullable = true)
    private String deposit;

//    @Column(name = "tecDocld", nullable = true)
    private String tecDocld;

//    @Column(name = "tecDocSupplierName", nullable = true)
    private String tecDocSupplierName;

//    @Column(name = "tecDocSupplierID", nullable = true)
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
                "tecDocld='" + tecDocld + '\'' + "\n" +
                "tecDocSupplierName='" + tecDocSupplierName + '\'' + "\n" +
                "tecDocSupplierID='" + tecDocSupplierID + '\'' + "\n" +
                '}';
    }
}