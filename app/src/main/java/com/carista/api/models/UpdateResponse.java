package com.carista.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import kotlin.jvm.Transient;

public class UpdateResponse implements Serializable {

    @SerializedName("current_version")
    @Expose
    public String currentVersion;

    @SerializedName("latest_version")
    @Expose
    public String latestVersion;

    @SerializedName("update")
    @Expose
    public boolean isLatestUpdate;
}
