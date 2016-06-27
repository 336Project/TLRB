package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/6/27.
 *
 */
public class BmobGeoPoint extends BaseEn{
    public static double EARTH_MEAN_RADIUS_KM = 6371.0D;
    public static double EARTH_MEAN_RADIUS_MILE = 3958.8D;
    private Double latitude = 0.0D;
    private Double longitude = 0.0D;
    private String __type = "GeoPoint";

    public BmobGeoPoint() {
    }

    public BmobGeoPoint(double longitude, double latitude) {
        this.setLongitude(longitude);
        this.setLatitude(latitude);
    }

    public void setLatitude(double latitude) {
        if(latitude <= 90.0D && latitude >= -90.0D) {
            this.latitude = latitude;
        } else {
            throw new IllegalArgumentException("Latitude must be within the range (-90.0, 90.0).");
        }
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double longitude) {
        if(longitude <= 180.0D && longitude >= -180.0D) {
            this.longitude = longitude;
        } else {
            throw new IllegalArgumentException("Longitude must be within the range (-180.0, 180.0).");
        }
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double distanceInRadiansTo(BmobGeoPoint point) {
        double var2 = this.latitude * 0.0174532925199433D;
        double var4 = this.longitude * 0.0174532925199433D;
        double var6 = point.getLatitude() * 0.0174532925199433D;
        double var8 = point.getLongitude() * 0.0174532925199433D;
        double var10 = var2 - var6;
        double var12 = var4 - var8;
        double var14 = Math.sin(var10 / 2.0D);
        double var16 = Math.sin(var12 / 2.0D);
        double var18 = var14 * var14 + Math.cos(var2) * Math.cos(var6) * var16 * var16;
        var18 = Math.min(1.0D, var18);
        return 2.0D * Math.asin(Math.sqrt(var18));
    }

    public double distanceInKilometersTo(BmobGeoPoint point) {
        return this.distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_KM;
    }

    public double distanceInMilesTo(BmobGeoPoint point) {
        return this.distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_MILE;
    }
}
