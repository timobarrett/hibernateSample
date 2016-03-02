package hibernate.weather.sample;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by tim_barrett on 2/15/2016.
 */
@Entity
@Table( name= "weather")
public class Weather implements Serializable{
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "_ID")
    private Integer id;

    @Column(name = "COLUMN_DATE", unique=true)
    private Long date;

    @ManyToOne(optional = false)
    @JoinColumn(name="COLUMN_LOC_KEY")
    private Location location;

    @Column(name="COLUMN_WEATHER_ID")
    private Integer weatherId;

    @Column(name="COLUMN_SHORT_DESC")
    private String description;

    @Column(name="COLUMN_TEMP")
    private Double temperature;

    @Column(name="COLUMN_HUMIDITY")
    private Double humidity;

    @Column(name="COLUMN_PRESSURE")
    private Double pressure;

    @Column(name="COLUMN_WIND_SPEED")
    private Double windSpeed;

    @Column(name="COLUMN_WIND_DEGREES")
    private Double windDirection;


    public Weather(){}


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public Long getDate() {
        return date;
    }
    public void setDate(Long date) {
        this.date = date;
    }


//    public Integer getLocKey()
//        return locKey;
//    }
//    public void setLocKey(Integer locKey) {
//        this.locKey = locKey;
//    }

    public Location getLocation() { return location;}
    public void setLocation(Location location) { this.location = location;}

    public Integer getWeatherId() {
        return weatherId;
    }
    public void setWeatherId(Integer weatherId) {
        this.weatherId = weatherId;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }


    public Double getHumidity() {
        return humidity;
    }
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPressure() {
        return pressure;
    }
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }


    public Double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getWindDirection() {
        return windDirection;
    }
    public void setWindDirection(Double windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * this prints out the contents of a db record
     * @return
     */
    @Override
    public String toString(){
        return "Weather [id = " + id + ", Date = " + date + ", Weather ID = " + weatherId + ", short description = " + description + ", temperature = " + temperature + ", humidity = " + humidity +"% ]";
    }

}
