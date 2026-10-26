package com.footballempire.career;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Local SQLite storage for career snapshots, match history, transfers, scouts and player records. */
public class CareerDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "football_empire_career.db";
    private static final int DB_VERSION = 1;

    public CareerDatabase(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE career_saves (id INTEGER PRIMARY KEY AUTOINCREMENT, club TEXT NOT NULL, budget INTEGER, played INTEGER, points INTEGER, morale INTEGER, formation TEXT, season_day INTEGER, saved_at INTEGER)");
        db.execSQL("CREATE TABLE matches (id INTEGER PRIMARY KEY AUTOINCREMENT, home TEXT, away TEXT, home_score INTEGER, away_score INTEGER, season_day INTEGER, played_at INTEGER)");
        db.execSQL("CREATE TABLE transfers (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT, club TEXT, fee INTEGER, direction TEXT, created_at INTEGER)");
        db.execSQL("CREATE TABLE scouting_reports (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT, position TEXT, rating INTEGER, potential INTEGER, market_value INTEGER, created_at INTEGER)");
        db.execSQL("CREATE TABLE players (id INTEGER PRIMARY KEY AUTOINCREMENT, external_id TEXT, name TEXT NOT NULL, club TEXT, position TEXT, age INTEGER, rating INTEGER, potential INTEGER, market_value INTEGER, contract_until TEXT)");
        db.execSQL("CREATE TABLE clubs (id INTEGER PRIMARY KEY AUTOINCREMENT, external_id TEXT, name TEXT NOT NULL, country TEXT, competition TEXT, crest_url TEXT)");
        db.execSQL("CREATE TABLE competitions (id INTEGER PRIMARY KEY AUTOINCREMENT, external_id TEXT, name TEXT NOT NULL, country TEXT, logo_url TEXT)");
        db.execSQL("CREATE TABLE fixtures (id INTEGER PRIMARY KEY AUTOINCREMENT, competition TEXT, home TEXT, away TEXT, match_day INTEGER, status TEXT, home_score INTEGER, away_score INTEGER)");
        db.execSQL("CREATE TABLE training (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT, training_type TEXT, rating_change INTEGER, season_day INTEGER)");
        db.execSQL("CREATE TABLE club_news (id INTEGER PRIMARY KEY AUTOINCREMENT, headline TEXT, body TEXT, season_day INTEGER, created_at INTEGER)");
        db.execSQL("CREATE INDEX idx_matches_day ON matches(season_day)");
        db.execSQL("CREATE INDEX idx_players_club ON players(club)");
        db.execSQL("CREATE INDEX idx_fixtures_day ON fixtures(match_day)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Versioned schema upgrades are intentionally explicit; never silently delete a career database.
    }

    public void saveCareer(String club, int budget, int played, int points, int morale, String formation, int seasonDay) {
        ContentValues v = new ContentValues();
        v.put("club", club); v.put("budget", budget); v.put("played", played); v.put("points", points);
        v.put("morale", morale); v.put("formation", formation); v.put("season_day", seasonDay); v.put("saved_at", System.currentTimeMillis());
        getWritableDatabase().insert("career_saves", null, v);
    }

    public void recordCompetition(String externalId, String name, String country, String logoUrl) {
        ContentValues v = new ContentValues(); v.put("external_id", externalId); v.put("name", name); v.put("country", country); v.put("logo_url", logoUrl);
        getWritableDatabase().insertWithOnConflict("competitions", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void recordClub(String externalId, String name, String country, String competition, String crestUrl) {
        ContentValues v = new ContentValues(); v.put("external_id", externalId); v.put("name", name); v.put("country", country); v.put("competition", competition); v.put("crest_url", crestUrl);
        getWritableDatabase().insertWithOnConflict("clubs", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void replacePlayers(String club, java.util.List<MainActivity.Player> players) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("players", "club=?", new String[]{club});
            for (MainActivity.Player p : players) {
                ContentValues v = new ContentValues(); v.put("name", p.name); v.put("club", club); v.put("position", p.position);
                v.put("age", p.age); v.put("rating", p.rating); v.put("potential", p.potential); v.put("market_value", p.value);
                db.insert("players", null, v);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    public void recordMatch(String home, String away, int homeScore, int awayScore, int seasonDay) {
        ContentValues v = new ContentValues();
        v.put("home", home); v.put("away", away); v.put("home_score", homeScore); v.put("away_score", awayScore);
        v.put("season_day", seasonDay); v.put("played_at", System.currentTimeMillis());
        getWritableDatabase().insert("matches", null, v);
    }

    public void recordTransfer(String player, String club, int fee, String direction) {
        ContentValues v = new ContentValues();
        v.put("player_name", player); v.put("club", club); v.put("fee", fee); v.put("direction", direction); v.put("created_at", System.currentTimeMillis());
        getWritableDatabase().insert("transfers", null, v);
    }

    public void recordScout(String name, String position, int rating, int potential, int value) {
        ContentValues v = new ContentValues();
        v.put("player_name", name); v.put("position", position); v.put("rating", rating); v.put("potential", potential);
        v.put("market_value", value); v.put("created_at", System.currentTimeMillis());
        getWritableDatabase().insert("scouting_reports", null, v);
    }
}