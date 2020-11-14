package com.regmode.bean;

import java.util.List;

/**
 * @Author: tobato
 * @Description: 作用描述  app 相关资料
 * @CreateDate: 2020/6/21 14:41
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/6/21 14:41
 */
public class AppInfoBean {
    /**
     * Result : ok
     * Model : [{"SoftwareName":"一见直播","SoftwareId":"YJZB","SoftwareType":"mb","SoftwareVersion":"1.1","softDownloadUrl":"/SoftwareManagement/Uploads/一见直播-2-1.1.0.apk","softDescription":"KEY=12345678"}]
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
         * SoftwareName : 一见直播
         * SoftwareId : YJZB
         * SoftwareType : mb
         * SoftwareVersion : 1.1
         * softDownloadUrl : /SoftwareManagement/Uploads/一见直播-2-1.1.0.apk
         * softDescription : KEY=12345678
         */

        private String SoftwareName;
        private String SoftwareId;
        private String SoftwareType;
        private String SoftwareVersion;
        private String softDownloadUrl;
        private String softDescription;

        public String getSoftwareName() {
            return SoftwareName;
        }

        public void setSoftwareName(String SoftwareName) {
            this.SoftwareName = SoftwareName;
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

        public String getSoftwareVersion() {
            return SoftwareVersion;
        }

        public void setSoftwareVersion(String SoftwareVersion) {
            this.SoftwareVersion = SoftwareVersion;
        }

        public String getSoftDownloadUrl() {
            return softDownloadUrl;
        }

        public void setSoftDownloadUrl(String softDownloadUrl) {
            this.softDownloadUrl = softDownloadUrl;
        }

        public String getSoftDescription() {
            return softDescription;
        }

        public void setSoftDescription(String softDescription) {
            this.softDescription = softDescription;
        }
    }
}
