package ca.mixitmedia.weaver.views;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BadgeData extends SQLiteOpenHelper {
    public static final String TABLE_BADGE = "badges";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ALIAS = "_alias";
    public static final String COLUMN_NAME = "category";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COLLECTED = "collected";
    public static final String COLUMN_AVAILABLE = "available";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BADGE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_ALIAS + " text not null, "
            + COLUMN_LATITUDE + " real not null,"
            + COLUMN_LONGITUDE + " real not null,"
            + COLUMN_DESCRIPTION + " text not null, "
            + COLUMN_COLLECTED + " integer not null"
            + ");";
    private Context context;

    public BadgeData(Context context){
        super(context, "weaver_tour", null, 1);
        this.context = context;

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        for(ContentValues cv : initialValues()){
            sqLiteDatabase.insert(TABLE_BADGE,null,cv);
        }

    }
    ContentValues CVFactory(String name, String alias, Double Lat, Double Long, String desc){
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_NAME, name );
        initialValues.put(COLUMN_ALIAS, alias);
        initialValues.put(COLUMN_LATITUDE, Lat);
        initialValues.put(COLUMN_LONGITUDE,Long);
        initialValues.put(COLUMN_DESCRIPTION,desc);
        int video = context.getResources().getIdentifier("raw/"+alias, null, context.getPackageName());
        initialValues.put(COLUMN_COLLECTED, (video==0?1:0));
        return initialValues;

    }

    List<ContentValues> initialValues(){
        return Arrays.asList(
            CVFactory("Rogers Communication Centre", "rcc",     43.658591, -79.377335,"The Rogers Communication Centre, or RCC, is the home of the RTA School of Media, Canada’s preeminent school of media content creation, innovation and broadcasting.  RCC is also the home of the Ryerson Transmedia Zone where teams of resident storytellers build innovative projects and push the boundaries storytelling using the coolest new technologies."),
            CVFactory("Ted Rogers School of Management", "trsm",43.655715, -79.382501,"Welcome to the Ted Rogers School of Management or TRSM as it's known on campus.  The school has a long history of providing top-quality business education in Toronto.  TRSM houses undergrad degrees such as Business, as well as Hospitality and Tourism Management, and more!  They also have a favourite as far as I am concerned,  Information Technology Management.  TRSM also offers a number of MBAs and an MMSc programs to in this roomy building that connects to Toronto's iconic Eaton Centre."),
                CVFactory("Engineering Building", "eng",        43.657563, -79.377239,"the Faculty of Engineering and Architectural Science's ENG building is the place to be if you want to jump start your career in Aerospace, Civil, Electrical and Computer Engineering... let's just say if you are an engineer in the making then this is the spot.  Here's an important tid bit for you game makers and the energy innovators.  This building is where the The Ryerson game makers union and the Centre for urban energy calls home."),
                CVFactory("G. Raymond Chang School", "chang",   43.657247, -79.379594,"This is the Chang School of Continuing Education.  With 70,000 enrolments each year.  The school boasts the title of Canada’s largest, most successful continuing education program.  The Change school is truly a leader in innovative, quality, lifelong learning."),
                CVFactory("School of Image Arts", "img",        43.657503, -79.379336,"The Image arts building is a signature feature of the Ryerson University campus.  At night time the building's choreographed LED lights illuminate the surrounding areas.  Students in Image arts are trained in film and photography at the bachelor level, as well as in Documentary Media, Film and Culture, and Photo Preservation at the graduate level.  If you appreciate art you can't go wrong checking out the Image arts gallery where world famous installations are often on display."),
                CVFactory("The Quad", "quad",                   43.658797, -79.379301,"Underneath the quad is the student gym."),
                CVFactory("Mattamy Athletic Centre", "mat",     43.661843, -79.380156,"The Mattamy Athletic Centre, once known as the historic Maple Leaf Gardens, is now the home of the proud Ryerson Rams!  Come here to check out a women's or men's basketball, hockey or volleyball.  Better yet come and get a work out in this classic building."),
                CVFactory("Student Centre", "scc",              43.657942, -79.378189,"If you are a student at Ryerson you need to know about the student centre.  You can get your discounted TTC pass here you can get ciniplex movie passes, passes, and even free food!"),
                CVFactory("Digital Media Zone", "dmz",          43.656568, -79.380406,"The Digital Media Zone is a flagship of the Ryerson University campus.  the DMZ as its called is an incubation space where innovative startups are fostered.  cohorts of young talent are given resources to launch their businesses.  There are over ... Companies currently in the DMZ.  As a result of the success of the incubation space Zones have been launched across the Ryerson campus"),
                CVFactory("Library", "rcc",                     43.657870, -79.380390,"There's books In here")
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(BadgeData.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGE);
        onCreate(sqLiteDatabase);    }
}
