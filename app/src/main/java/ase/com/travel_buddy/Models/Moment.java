package ase.com.travel_buddy.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import java.io.Serializable;

@Entity(tableName = "moments")
public class Moment implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String api_id;
    private String user_id;
    private String description;
    private String image;
    private String icon;
    private Double latitude;
    private Double longitude;
    private Long created_at;
    private Long updated_at;

    public static final String TABLE_NAME = "moments";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_API_ID = "api_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static Moment fromContentValues(ContentValues contentValues) {
        final Moment moment = new Moment();

        if (contentValues.containsKey(COLUMN_API_ID)) {
            moment.api_id = contentValues.getAsString(COLUMN_API_ID);
        }
        if (contentValues.containsKey(COLUMN_USER_ID)) {
            moment.user_id = contentValues.getAsString(COLUMN_USER_ID);
        }
        if (contentValues.containsKey(COLUMN_DESCRIPTION)) {
            moment.description = contentValues.getAsString(COLUMN_DESCRIPTION);
        }
        if (contentValues.containsKey(COLUMN_IMAGE)) {
            moment.image = contentValues.getAsString(COLUMN_IMAGE);
        }
        if (contentValues.containsKey(COLUMN_ICON)) {
            moment.icon = contentValues.getAsString(COLUMN_ICON);
        }
        if (contentValues.containsKey(COLUMN_LATITUDE)) {
            moment.latitude = contentValues.getAsDouble(COLUMN_LATITUDE);
        }
        if (contentValues.containsKey(COLUMN_LONGITUDE)) {
            moment.longitude = contentValues.getAsDouble(COLUMN_LONGITUDE);
        }
        if (contentValues.containsKey(COLUMN_CREATED_AT)) {
            moment.created_at = contentValues.getAsLong(COLUMN_CREATED_AT);
        }
        if (contentValues.containsKey(COLUMN_UPDATED_AT)) {
            moment.updated_at = contentValues.getAsLong(COLUMN_UPDATED_AT);
        }
        return moment;
    }

    public Moment(){}

    @Ignore
    public Moment(Long id, String api_id, String user_id, String description, String image, String icon, Double latitude, Double longitude, Long created_at, Long updated_at) {
        this.id = id;
        this.api_id = api_id;
        this.user_id = user_id;
        this.description = description;
        this.image = image;
        this.icon = icon;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Moment(String description, Double latitude, Double longitude) {
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setApi_id(String api_id) {
        this.api_id = api_id;
    }

    public String getApi_id() {
        return api_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }
}
