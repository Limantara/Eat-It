package me.limantara.eatit.model;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class Venue implements Serializable {

    public String locu_id;
    public String name;
    public Location location;
    public List<Menu> menus;
    public List<Category> categories;
    private String[] excludedKeywords;
    private String displayAddress;
    private Float latitude;
    private Float longitude;

    public Venue() {
        excludedKeywords = new String[]{ "Drink", "Appetizer", "Dessert",
                "Wine", "Beer", "Sake", "Champagne" };
    }

    public Venue(Cursor cursor) {
        locu_id = cursor.getString(0);
        name = cursor.getString(1);
        displayAddress = cursor.getString(2); System.out.println(location);
        latitude = cursor.getFloat(3);
        longitude = cursor.getFloat(4);
    }

    public void setLatitude(Float lat) {
        latitude = lat;
    }

    public void setLongitude(Float lngt) {
        longitude = lngt;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public List<Venue.Item> getFoods(int budget) {
        List<Venue.Item> items = new ArrayList<>();

        for(Venue.Menu menu : menus) {
            for(Venue.Section section : menu.sections) {
                if(section.section_name != null && isValid(section.section_name)) {
                    for(Venue.Subsection subsection : section.subsections) {
                        if(subsection.contents != null && isValid(subsection.subsection_name)) {
                            for(Item food : subsection.contents) {
                                if(food.price != null && isNumeric(food.price)) {

                                    double price = Double.parseDouble(food.price);
                                    int truncatedPrice = (int) price;

                                    if(truncatedPrice < budget)
                                        items.add(food);
                                }
                            }
                        }
                    }
                }
            }
        }

        return items;
    }


    private static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public String getDisplayAddress() {
        displayAddress = "";

        if(Location.address1 != null)
            displayAddress += Location.address1 + "\n";
        if(Location.address2 != null)
            displayAddress += Location.address2 + "\n";
        if(Location.address3 != null)
            displayAddress += Location.address3 + "\n";
        if(Location.locality != null)
            displayAddress += Location.locality + "\n";

        return displayAddress;
    }

    private boolean isValid(String name) {
        for(String keyword : excludedKeywords) {
            if(name.contains(keyword))
                return false;
        }

        return true;
    }

    public String toString() {
        return name + " | location: " + location.address1 + ", " + location.locality;
    }

    public static class Location implements Serializable {
        public static String address1;
        public static String address2;
        public static String address3;
        public static String locality;
        public static String region;
        public static String postal_code;
        public static String country;
        public static GeoJSON geo;

        public String toString() {
            return "locality: " + locality;
        }
    }

    public static class GeoJSON implements Serializable {
        public String type;
        public Geometry geometry;
        public Property properties;

        public static class Geometry implements Serializable {
            public String type;
            public List<Float> coordinates;
        }

        public static class Property implements Serializable {
            public String name;
        }
    }

    public static class Menu implements Serializable {
        public String menu_name;
        public List<Section> sections;

        public String toString() {
            return menu_name;
        }
    }

    public static class Section implements Serializable {
        public String section_name;
        public List<Subsection> subsections;

        public String toString() {
            return section_name;
        }
    }

    public static class Subsection implements Serializable {
        public String subsection_name;
        public List<Item> contents;

        public String toString() {
            return subsection_name;
        }
    }

    public static class Item implements Serializable {
        public String price;
        public String name;
        public String description;
        public List<String> images;
        public Long created_at;
        public String venue_locu_id;
        public int eat_it;
        public int expired;

        public Item() {}

        public Item(Cursor cursor) {
            name = cursor.getString(1);
            price = cursor.getString(2);
            description = cursor.getString(3);
            images = new ArrayList<>();
            images.add(cursor.getString(4));
            created_at = Long.parseLong(cursor.getString(5));
            venue_locu_id = cursor.getString(6);
            eat_it = Integer.parseInt(cursor.getString(7));
            expired = Integer.parseInt(cursor.getString(8));
        }

        public String toString() {
            return name;
        }
    }

    public static class Category implements Serializable {
        public String name;
        public String str_id;

        public String toString() {
            return name;
        }
    }
}
