package com.george.tcsdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Lista implements Parcelable {
    private String dt;

    private String dt_txt;

    private Weather[] weather;

    private Main main;

    /*private Clouds clouds;*/

    /*private Sys sys;*/

    /*private Wind wind;*/


    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

   /* public Clouds getClouds ()
    {
        return clouds;
    }

    public void setClouds (Clouds clouds)
    {
        this.clouds = clouds;
    }*/

    /*public Sys getSys ()
    {
        return sys;
    }

    public void setSys (Sys sys)
    {
        this.sys = sys;
    }

    public Wind getWind ()
    {
        return wind;
    }

    public void setWind (Wind wind)
    {
        this.wind = wind;
    }*/

    @Override
    public String toString() {
        return "ClassPojo [dt = " + dt + ", dt_txt = " + dt_txt + ", weather = " + weather + ", main = " + main + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Lista(Parcel in) {}

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

    public static final Creator<Lista> CREATOR = new Creator<Lista>() {
        @Override
        public Lista createFromParcel(Parcel in) {
            return new Lista(in);
        }

        @Override
        public Lista[] newArray(int size) {
            return new Lista[size];
        }
    };
}
