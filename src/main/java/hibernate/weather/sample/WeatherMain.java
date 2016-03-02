package hibernate.weather.sample;

/**
 * Created by tim_barrett on 2/16/2016.
 */
import java.net.URISyntaxException;
import java.net.URI;
import java.util.*;

import org.apache.http.client.utils.URIBuilder;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by tim_barrett on 9/2015.
 *   get the daily weather using latitude and longitude
 * http://api.openweathermap.org/data/2.5/weather?lat=42.831&lon=-71.569&units=imperial - not fine grained enough
 *    get the daily weather forecast using zipcode / post code
 * http://api.openweathermap.org/data/2.5/forecast?zip=03031 - returns forecasted data
 *    get the weather for cast for 7 days using zip code
 * http://api.openweathermap.org/data/2.5/forecast/daily?q=03031&mode=json&units=metric&cnt=7
 */
public class WeatherMain {

    public static String LON = "lon";
    public static String ZIP = "zip";
    public static String LAT = "lat";
    public static String APPID = "appid";
    public static String APPID_VALUE = "c6ad2b992ee70977f33877df07c9dc0f";

    public static String TAG_NAME = "main";
    public static restClientFunc restFunction;
    public static String OPEN_WEATHER_URL = "api.openweathermap.org";
    public static String OPEN_WEATHER_PATH = "/data/2.5/forecast/daily";
    public static String OPEN_WEATHER_PATH_FRCAST = "/data/2.5/weather";
    public static String MODE = "mode";
    public static String MODE_VALUE = "json";
    public static String UNIT = "units";
    public static String UNIT_VALUE = "imperial";
    public static String CNT = "cnt";
    public static String CNT_VALUE = "7";

    private Weather weatherData;
    private Location locationData;
    public enum forecast {dailyZip, dailyLonLat, sevenDay}

    private SessionFactory sessionFactory = null;

    forecast mForecast;

    //  public static String dbName = "http://127.0.0.1:5984/weather";
    //http://api.openweathermap.org/data/2.5/forecast/daily?appid=0da6960ae510202d1f8633e08e075162&zip=03031&mode=json&units=imperial&cnt=7

    /**
     * Entry point and a basic means to test all methods.
     *
     * @param args - zip code is passed in as parameter
     */
    public static void main(String[] args) {

        WeatherMain weather = new WeatherMain();
        restFunction = new restClientFunc();
        weather.setupHibernate();

        String weatherResult = weather.getWeatherForecast(args[0], args[1], args[2]);
        System.out.println("RESULTS HERE = " + weatherResult);
        weather.loadReportWeather(weatherResult);
        System.out.println("RESULTS = " + weatherResult);
        System.out.println("Weather Records : ");
        weather.getAllDbRecords();
        weather.sessionFactory.close();
    }

    /**
     * quick and dirty way of creating a sessionFactory
     */
    void setupHibernate(){
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
    }
    /**
     * make the rest call to openweathermap and return the json output
     *
     * @return
     * @params zip code,
     * longitude
     * latitude
     */
    public String getWeatherForecast(String... params) {

        ArrayList<String> results;
        // build URI and get daily forecast using zipcode
        URI zipUri = buildWeatherForecastUrl(forecast.dailyZip, params[0]);
        results = restFunction.restGet(zipUri.toString());

        results.clear();
        //build URI and get 7 day forecast
        URI zip7Uri = buildWeatherForecastUrl(forecast.sevenDay, params[0]);
        results = restFunction.restGet(zip7Uri.toString());

        results.clear();
        //build URI and get daily forecast using longitude and latitude
        URI lonLatUri = buildWeatherForecastUrl(forecast.dailyLonLat, params[1], params[2]);
        results = restFunction.restGet(lonLatUri.toString());

        return results.get(0);

   }

    /**
     * get daily weather and 7 day forecast
     *
     * @param forecastType
     * @param param
     * @return
     */
    public URI buildWeatherForecastUrl(forecast forecastType, String... param) {
        ArrayList<String> results;
        URIBuilder builtUri;
        URI uri = null;
        switch (forecastType) {
            case dailyLonLat:
                try {
                    builtUri = new URIBuilder()
                            .setScheme("http")
                            .setHost(OPEN_WEATHER_URL)
                            .setPath(OPEN_WEATHER_PATH_FRCAST)
                            .addParameter(LAT, param[0])
                            .addParameter(LON, param[1])
                            .addParameter(APPID, APPID_VALUE)
                            .addParameter(MODE, MODE_VALUE)
                            .addParameter(UNIT, UNIT_VALUE);
                    uri = builtUri.build();
                } catch (URISyntaxException u) {
                    System.out.println("ERROR - URI EXCEPTION");
                }
                break;
            case dailyZip:
                try {
                    builtUri = new URIBuilder()
                            .setScheme("http")
                            .setHost(OPEN_WEATHER_URL)
                            .setPath(OPEN_WEATHER_PATH_FRCAST)
                            .addParameter(ZIP, param[0])
                            .addParameter(APPID, APPID_VALUE)
                            .addParameter(MODE, MODE_VALUE)
                            .addParameter(UNIT, UNIT_VALUE);
                    uri = builtUri.build();
                } catch (URISyntaxException u) {
                    System.out.println("ERROR - URI EXCEPTION");
                }
                break;
            case sevenDay:
                try {
                    builtUri = new URIBuilder()
                            .setScheme("http")
                            .setHost(OPEN_WEATHER_URL)
                            .setPath(OPEN_WEATHER_PATH)
                            .addParameter(ZIP, param[0])
                            .addParameter(APPID, APPID_VALUE)
                            .addParameter(MODE, MODE_VALUE)
                            .addParameter(UNIT, UNIT_VALUE)
                            .addParameter(CNT, CNT_VALUE);
                    uri = builtUri.build();
                } catch (URISyntaxException u) {
                    System.out.println("ERROR - URI EXCEPTION");
                }
                break;
        }
        if (uri != null) {
            System.out.println("BUILT URI = " + uri.toString());
        }
        return uri;
    }

    /**
     * loadReportWeather - parse the json string for data
     * shows json array and sub key handling
     *
     * @param jsonBuf
     * @return
     */
    public String loadReportWeather(String jsonBuf) {
        JSONParser parser = new JSONParser();
        System.out.println("loadReportWeather = " + jsonBuf);
        Object obj = null;
        weatherData = new Weather();
        locationData = new Location();

        try {
            obj = parser.parse(jsonBuf);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject weObj = (JSONObject) jsonObject.get(TAG_NAME);
            weatherData.setHumidity(Double.valueOf(weObj.get("humidity").toString()));
            weatherData.setTemperature(Double.valueOf(weObj.get("temp").toString()));
            weatherData.setPressure(Double.valueOf(weObj.get("pressure").toString()));
            System.out.println("Temp = " + weObj.get("temp") + " Humidity = " + weObj.get("humidity") + "%"+ " Pressure =" + weObj.get("pressure"));
            JSONObject locObj = (JSONObject) jsonObject.get("coord");
            System.out.println("LONG/LAT = " + locObj.get("lon") + " / " + locObj.get("lat"));
            JSONArray conf = (JSONArray) jsonObject.get("weather");
            JSONObject weaObj = (JSONObject) conf.get(0);
            JSONObject windObj = (JSONObject) jsonObject.get("wind");
            weatherData.setWindSpeed((Double)windObj.get("speed"));
            if (windObj.containsKey("deg")) {
                weatherData.setWindDirection(Double.valueOf(windObj.get("deg").toString()));
            }else{weatherData.setWindDirection(Double.valueOf(0));}
            System.out.println("Wind speed = " + windObj.get("speed") + "Degrees = "+ windObj.get("deg"));
            JSONObject location = (JSONObject) jsonObject.get("sys");
            System.out.println("Run from " + jsonObject.get("name") + " " + location.get("country"));
            weatherData.setDescription(weaObj.get("description").toString());
            System.out.println("Condition = " + weaObj.get("main") + " Detail = " + weaObj.get("description"));
            weatherData.setWeatherId(Integer.valueOf(jsonObject.get("id").toString()));
            weatherData.setDate(normalizeDate());

            locationData.setCityName(jsonObject.get("name").toString());
            locationData.setLatCoord((Double)locObj.get("lat"));
            locationData.setLonCoord((Double)locObj.get("lon"));
            weatherData.setLocation(locationData); // KEY to no foreign key when adding without this
        } catch (ParseException e) {
            e.printStackTrace();
        }
        addWeatherToDatabase();

        return (" ");
    }

    /**
     * Set a year month day date in milliseconds.  Provides 1 record per day
     * @return
     */
    public static long normalizeDate() {
        Calendar todaysDate = Calendar.getInstance();
        System.out.println("DAY = " + todaysDate.get(Calendar.DAY_OF_MONTH) + "MONTH = " + todaysDate.get(Calendar.MONTH) + "YEAR = " + todaysDate.get(Calendar.YEAR));
        Calendar returnedDate = new GregorianCalendar(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH), todaysDate.get(Calendar.DAY_OF_MONTH));
        System.out.println("returnedDate = " + returnedDate.getTimeInMillis());
        return returnedDate.getTimeInMillis();
    }

    /**
     * adds database data by persisting the data, commiting and closing the transaction
     */
  public void addWeatherToDatabase(){
      Session session = sessionFactory.openSession();
      session.beginTransaction();
      session.persist(locationData);
      if(!doesWeatherRecordExist(weatherData.getDate(),session)){
          session.persist(weatherData);
          System.out.println("JOY\n");
      }else{
          String query = "from Weather where COLUMN_DATE = :weatherDate";
          weatherData.setDescription("TESTY");
          try {
                Weather curWeather = (Weather)session.createQuery(query).setParameter("weatherDate",weatherData.getDate()).uniqueResult();
                curWeather.setWeatherId(weatherData.getWeatherId());
                curWeather.setDescription(weatherData.getDescription());
                curWeather.setHumidity(weatherData.getHumidity());
                curWeather.setPressure(weatherData.getPressure());
                curWeather.setTemperature(weatherData.getTemperature());
                curWeather.setWindDirection(weatherData.getWindDirection());
                curWeather.setWindSpeed(weatherData.getWindSpeed());
            } catch(HibernateException e){
                System.out.println("ERROR - updating weather = "+e.getLocalizedMessage());
            }

      }
      session.getTransaction().commit();
      session.close();
  }

    /**
     *  doesWeatherRecordExist
     *      if found then database record is fetched and updated
     * @param wDate
     * @param session
     * @return
     */
    public boolean doesWeatherRecordExist(Long wDate, Session session){
        StringBuilder query = new StringBuilder("from Weather where ");
        query.append("COLUMN_DATE = '" + wDate + "'");
        Query result = session.createQuery(query.toString());
        return (result.list().size()>0?true:false);
    }
    /**
     * get all the weather and location records and System.out them
     */
    public void getAllDbRecords() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Weather> weather = (List<Weather>)session.createQuery("from Weather ").list();
        for( Weather w: weather){
            System.out.println("Weather record : "+w );
            System.out.println("Weather Location Details: "+w.getLocation());
        }

        session.getTransaction().commit();
        session.close();
    }

  }