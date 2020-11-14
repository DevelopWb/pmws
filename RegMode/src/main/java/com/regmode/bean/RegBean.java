package com.regmode.bean;

import java.util.List;

/**
 * Author:wang_sir
 * Time:2019/12/25 11:41
 * Description:This is RegBean
 */
public class RegBean {


    /**
     * Result : ok
     * Model : [{"SoftType":"mb","Id":"2879","Name":"取证终端用户端APP","Identification":"QZYH","Version":"1.0","Customer":"王斌","isModel":"1","Model":"","isValid":"1","ValidStart":"","ValidEnd":"","isNumber":"1","Number":"","NumberNow":"0","RegisCode":"7QzZE90r","RegisCodeState":"正常","isDisabled":"1","RegisCodeAddDateTime":"2019/12/24 22:13:24","isAutoUpdate":"0","isToolTip":"","isMAC":"","MAC":"","Id1":"399","customerName":"王斌","customerPhoneNum":"18810211734","customerDirector":"王斌","customerDescription":"取证用户端","customerAddDateTime":"1900/1/1 0:00:00"}]
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
         * SoftType : mb
         * Id : 2879
         * Name : 取证终端用户端APP
         * Identification : QZYH
         * Version : 1.0
         * Customer : 王斌
         * isModel : 1
         * Model :
         * isValid : 1
         * ValidStart :
         * ValidEnd :
         * isNumber : 1
         * Number :
         * NumberNow : 0
         * RegisCode : 7QzZE90r
         * RegisCodeState : 正常
         * isDisabled : 1
         * RegisCodeAddDateTime : 2019/12/24 22:13:24
         * isAutoUpdate : 0
         * isToolTip :
         * isMAC :
         * MAC :
         * Id1 : 399
         * customerName : 王斌
         * customerPhoneNum : 18810211734
         * customerDirector : 王斌
         * customerDescription : 取证用户端
         * customerAddDateTime : 1900/1/1 0:00:00
         */

        private String SoftType;
        private String Id;
        private String Name;
        private String Identification;
        private String Version;
        private String Customer;
        private String isModel;
        private String Model;
        private String isValid;
        private String ValidStart;
        private String ValidEnd;
        private String isNumber;
        private String Number;
        private String NumberNow;
        private String RegisCode;
        private String RegisCodeState;
        private String isDisabled;
        private String RegisCodeAddDateTime;
        private String isAutoUpdate;
        private String isToolTip;
        private String isMAC;
        private String MAC;
        private String Id1;
        private String customerName;
        private String customerPhoneNum;
        private String customerDirector;
        private String customerDescription;
        private String customerAddDateTime;

        public String getSoftType() {
            return SoftType == null ? "" : SoftType;
        }

        public void setSoftType(String softType) {
            SoftType = softType;
        }

        public String getId() {
            return Id == null ? "" : Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getName() {
            return Name == null ? "" : Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getIdentification() {
            return Identification == null ? "" : Identification;
        }

        public void setIdentification(String identification) {
            Identification = identification;
        }

        public String getVersion() {
            return Version == null ? "" : Version;
        }

        public void setVersion(String version) {
            Version = version;
        }

        public String getCustomer() {
            return Customer == null ? "" : Customer;
        }

        public void setCustomer(String customer) {
            Customer = customer;
        }

        public String getIsModel() {
            return isModel == null ? "" : isModel;
        }

        public void setIsModel(String isModel) {
            this.isModel = isModel;
        }

        public String getModel() {
            return Model == null ? "" : Model;
        }

        public void setModel(String model) {
            Model = model;
        }

        public String getIsValid() {
            return isValid == null ? "" : isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getValidStart() {
            return ValidStart == null ? "" : ValidStart;
        }

        public void setValidStart(String validStart) {
            ValidStart = validStart;
        }

        public String getValidEnd() {
            return ValidEnd == null ? "" : ValidEnd;
        }

        public void setValidEnd(String validEnd) {
            ValidEnd = validEnd;
        }

        public String getIsNumber() {
            return isNumber == null ? "" : isNumber;
        }

        public void setIsNumber(String isNumber) {
            this.isNumber = isNumber;
        }

        public String getNumber() {
            return Number == null ? "" : Number;
        }

        public void setNumber(String number) {
            Number = number;
        }

        public String getNumberNow() {
            return NumberNow == null ? "" : NumberNow;
        }

        public void setNumberNow(String numberNow) {
            NumberNow = numberNow;
        }

        public String getRegisCode() {
            return RegisCode == null ? "" : RegisCode;
        }

        public void setRegisCode(String regisCode) {
            RegisCode = regisCode;
        }

        public String getRegisCodeState() {
            return RegisCodeState == null ? "" : RegisCodeState;
        }

        public void setRegisCodeState(String regisCodeState) {
            RegisCodeState = regisCodeState;
        }

        public String getIsDisabled() {
            return isDisabled == null ? "" : isDisabled;
        }

        public void setIsDisabled(String isDisabled) {
            this.isDisabled = isDisabled;
        }

        public String getRegisCodeAddDateTime() {
            return RegisCodeAddDateTime == null ? "" : RegisCodeAddDateTime;
        }

        public void setRegisCodeAddDateTime(String regisCodeAddDateTime) {
            RegisCodeAddDateTime = regisCodeAddDateTime;
        }

        public String getIsAutoUpdate() {
            return isAutoUpdate == null ? "" : isAutoUpdate;
        }

        public void setIsAutoUpdate(String isAutoUpdate) {
            this.isAutoUpdate = isAutoUpdate;
        }

        public String getIsToolTip() {
            return isToolTip == null ? "" : isToolTip;
        }

        public void setIsToolTip(String isToolTip) {
            this.isToolTip = isToolTip;
        }

        public String getIsMAC() {
            return isMAC == null ? "" : isMAC;
        }

        public void setIsMAC(String isMAC) {
            this.isMAC = isMAC;
        }

        public String getMAC() {
            return MAC == null ? "" : MAC;
        }

        public void setMAC(String MAC) {
            this.MAC = MAC;
        }

        public String getId1() {
            return Id1 == null ? "" : Id1;
        }

        public void setId1(String id1) {
            Id1 = id1;
        }

        public String getCustomerName() {
            return customerName == null ? "" : customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerPhoneNum() {
            return customerPhoneNum == null ? "" : customerPhoneNum;
        }

        public void setCustomerPhoneNum(String customerPhoneNum) {
            this.customerPhoneNum = customerPhoneNum;
        }

        public String getCustomerDirector() {
            return customerDirector == null ? "" : customerDirector;
        }

        public void setCustomerDirector(String customerDirector) {
            this.customerDirector = customerDirector;
        }

        public String getCustomerDescription() {
            return customerDescription == null ? "" : customerDescription;
        }

        public void setCustomerDescription(String customerDescription) {
            this.customerDescription = customerDescription;
        }

        public String getCustomerAddDateTime() {
            return customerAddDateTime == null ? "" : customerAddDateTime;
        }

        public void setCustomerAddDateTime(String customerAddDateTime) {
            this.customerAddDateTime = customerAddDateTime;
        }
    }
}
