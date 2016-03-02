package hibernate.weather.sample;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by tim_barrett on 2/15/2016.
 */
@Entity
@Table( name= "location")
public class Location implements Serializable{
    private Integer id;
    private String cityName;
    private Double latCoord;
    private Double lonCoord;

    public Location(){}

    public Location(Integer id){
        this.id = id;
    }

    @Id
    @GeneratedValue
    @Column(name="_ID")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="COLUMN_WEATHER_COORD_LON")
    public Double getLonCoord() {
        return lonCoord;
    }
    public void setLonCoord(Double lonCoord) {
        this.lonCoord = lonCoord;
    }


    @Column(name="COLUMN_WEATHER_CITY")
    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Column(name="COLUM_WEATHER_COORD_LAT")
    public Double getLatCoord() {
        return latCoord;
    }
    public void setLatCoord(Double latCoord) {
        this.latCoord = latCoord;
    }

    /**
     * this prints out the contents of the location table record
     * @return
     */
    @Override
    public String toString(){
        return "Location [id=" + id + ", City = "+ cityName + ", Latitude = "+ latCoord + ", Longitude = " + lonCoord + "]";
    }
}
