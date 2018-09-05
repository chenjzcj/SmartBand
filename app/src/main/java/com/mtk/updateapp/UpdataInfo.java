package com.mtk.updateapp;

/**
 * 版本更新信息
 */
public class UpdataInfo {
    private String version;
    private String url;
    private String description;
    private String url_server;
    private String force;//是否强制更新版本

    public String getUrl_server() {
        return url_server;
    }

    public void setUrl_server(String url_server) {
        this.url_server = url_server;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    @Override
    public String toString() {
        return "UpdataInfo{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", url_server='" + url_server + '\'' +
                ", force='" + force + '\'' +
                '}';
    }
}