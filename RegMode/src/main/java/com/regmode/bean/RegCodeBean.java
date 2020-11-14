package com.regmode.bean;

import java.util.List;

/**
 * @Author: tobato
 * @Description: 作用描述   新注册码接口 注册码信息
 * @CreateDate: 2020/6/18 21:36
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/6/18 21:36
 */
public class RegCodeBean {

    /**
     * Result : ok
     * Model : [{"RegisCode":"DFQXJlwt","Imei":"","SoftwareId":"YJZB","SoftwareType":"mb","Name":"一见直播","Version":"1.1","Customer":"测试王","isImei":"1","isValid":"1","ValidStart":"","ValidEnd":"","isNumber":"1","Number":"","NumberNow":"0","RegisCodeState":"正常","isDisabled":"1","RegisCodeAddDateTime":"2020/6/20 21:53:08","isAutoUpdate":"0","isMAC":"0","MAC":"","isToolTip":"0","Id":"41","customerName":"测试王","customerPhoneNum":"15311810032","customerDirector":"王sir","customerDescription":"ceshi","customerAddDateTime":"1900/1/1 0:00:00"}]
     */

    private String Result;
    private List<ModelBean> Model;

    public String getResult() {
        return Result;
    }

    public void setResult(String Result) {
        this.Result = Result;
    }

    public List<ModelBean> getModel() {
        return Model;
    }

    public void setModel(List<ModelBean> Model) {
        this.Model = Model;
    }

    public static class ModelBean {
        /**
         * RegisCode : DFQXJlwt
         * Imei :
         * SoftwareId : YJZB
         * SoftwareType : mb
         * Name : 一见直播
         * Version : 1.1
         * Customer : 测试王
         * isImei : 1
         * isValid : 1
         * ValidStart :
         * ValidEnd :
         * isNumber : 1
         * Number :
         * NumberNow : 0
         * RegisCodeState : 正常
         * isDisabled : 1
         * RegisCodeAddDateTime : 2020/6/20 21:53:08
         * isAutoUpdate : 0
         * isMAC : 0
         * MAC :
         * isToolTip : 0
         * Id : 41
         * customerName : 测试王
         * customerPhoneNum : 15311810032
         * customerDirector : 王sir
         * customerDescription : ceshi
         * customerAddDateTime : 1900/1/1 0:00:00
         */

        private String RegisCode;
        private String Imei;
        private String SoftwareId;
        private String SoftwareType;
        private String Name;
        private String Version;
        private String Customer;
        private String isImei;
        private String isValid;
        private String ValidStart;
        private String ValidEnd;
        private String isNumber;
        private String Number;
        private String NumberNow;
        private String RegisCodeState;
        private String isDisabled;
        private String RegisCodeAddDateTime;
        private String isAutoUpdate;
        private String isMAC;
        private String MAC;
        private String isToolTip;
        private String Id;
        private String customerName;
        private String customerPhoneNum;
        private String customerDirector;
        private String customerDescription;
        private String customerAddDateTime;

        public String getRegisCode() {
            return RegisCode;
        }

        public void setRegisCode(String RegisCode) {
            this.RegisCode = RegisCode;
        }

        public String getImei() {
            return Imei;
        }

        public void setImei(String Imei) {
            this.Imei = Imei;
        }

        public String getSoftwareId() {
            return SoftwareId;
        }

        public void setSoftwareId(String SoftwareId) {
            this.SoftwareId = SoftwareId;
        }

        public String getSoftwareType() {
            return SoftwareType;
        }

        public void setSoftwareType(String SoftwareType) {
            this.SoftwareType = SoftwareType;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public String getCustomer() {
            return Customer;
        }

        public void setCustomer(String Customer) {
            this.Customer = Customer;
        }

        public String getIsImei() {
            return isImei;
        }

        public void setIsImei(String isImei) {
            this.isImei = isImei;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getValidStart() {
            return ValidStart;
        }

        public void setValidStart(String ValidStart) {
            this.ValidStart = ValidStart;
        }

        public String getValidEnd() {
            return ValidEnd;
        }

        public void setValidEnd(String ValidEnd) {
            this.ValidEnd = ValidEnd;
        }

        public String getIsNumber() {
            return isNumber;
        }

        public void setIsNumber(String isNumber) {
            this.isNumber = isNumber;
        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String Number) {
            this.Number = Number;
        }

        public String getNumberNow() {
            return NumberNow;
        }

        public void setNumberNow(String NumberNow) {
            this.NumberNow = NumberNow;
        }

        public String getRegisCodeState() {
            return RegisCodeState;
        }

        public void setRegisCodeState(String RegisCodeState) {
            this.RegisCodeState = RegisCodeState;
        }

        public String getIsDisabled() {
            return isDisabled;
        }

        public void setIsDisabled(String isDisabled) {
            this.isDisabled = isDisabled;
        }

        public String getRegisCodeAddDateTime() {
            return RegisCodeAddDateTime;
        }

        public void setRegisCodeAddDateTime(String RegisCodeAddDateTime) {
            this.RegisCodeAddDateTime = RegisCodeAddDateTime;
        }

        public String getIsAutoUpdate() {
            return isAutoUpdate;
        }

        public void setIsAutoUpdate(String isAutoUpdate) {
            this.isAutoUpdate = isAutoUpdate;
        }

        public String getIsMAC() {
            return isMAC;
        }

        public void setIsMAC(String isMAC) {
            this.isMAC = isMAC;
        }

        public String getMAC() {
            return MAC;
        }

        public void setMAC(String MAC) {
            this.MAC = MAC;
        }

        public String getIsToolTip() {
            return isToolTip;
        }

        public void setIsToolTip(String isToolTip) {
            this.isToolTip = isToolTip;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerPhoneNum() {
            return customerPhoneNum;
        }

        public void setCustomerPhoneNum(String customerPhoneNum) {
            this.customerPhoneNum = customerPhoneNum;
        }

        public String getCustomerDirector() {
            return customerDirector;
        }

        public void setCustomerDirector(String customerDirector) {
            this.customerDirector = customerDirector;
        }

        public String getCustomerDescription() {
            return customerDescription;
        }

        public void setCustomerDescription(String customerDescription) {
            this.customerDescription = customerDescription;
        }

        public String getCustomerAddDateTime() {
            return customerAddDateTime;
        }

        public void setCustomerAddDateTime(String customerAddDateTime) {
            this.customerAddDateTime = customerAddDateTime;
        }
    }
}
