extends Node
class_name CareerDatabase

var db: SQLite
var ready := false

func _ready() -> void:
	db = SQLite.new()
	db.path = "user://football_empire.db"
	if db.open_db():
		ready = true
		_create_tables()

func _create_tables() -> void:
	for sql in [
		"CREATE TABLE IF NOT EXISTS career_saves (id INTEGER PRIMARY KEY, club TEXT, season INTEGER, week INTEGER, budget INTEGER, points INTEGER, payload TEXT, updated_at TEXT DEFAULT CURRENT_TIMESTAMP)",
		"CREATE TABLE IF NOT EXISTS matches (id INTEGER PRIMARY KEY AUTOINCREMENT, season INTEGER, round_no INTEGER, home TEXT, away TEXT, home_goals INTEGER, away_goals INTEGER, points_after INTEGER)",
		"CREATE TABLE IF NOT EXISTS transfers (id INTEGER PRIMARY KEY AUTOINCREMENT, club TEXT, player TEXT, direction TEXT, fee INTEGER, week INTEGER)",
		"CREATE TABLE IF NOT EXISTS scouting_reports (id INTEGER PRIMARY KEY AUTOINCREMENT, region TEXT, report TEXT, week INTEGER)",
		"CREATE TABLE IF NOT EXISTS clubs (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, competition TEXT, rating INTEGER)",
		"CREATE TABLE IF NOT EXISTS competitions (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, country TEXT)",
		"CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, club TEXT, position TEXT, age INTEGER, overall INTEGER, potential INTEGER, value INTEGER, wage INTEGER)",
		"CREATE TABLE IF NOT EXISTS fixtures (id INTEGER PRIMARY KEY AUTOINCREMENT, season INTEGER, round_no INTEGER, home TEXT, away TEXT, score TEXT, played INTEGER)",
		"CREATE TABLE IF NOT EXISTS training (id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, focus TEXT, week INTEGER, improvement INTEGER)",
		"CREATE TABLE IF NOT EXISTS club_news (id INTEGER PRIMARY KEY AUTOINCREMENT, week INTEGER, headline TEXT)"
	]:
		db.query(sql)

func saveCareer(id:int,club:String,season:int,week:int,budget:int,points:int,payload:String) -> void:
	if not ready: return
	db.query_with_bindings("INSERT OR REPLACE INTO career_saves(id,club,season,week,budget,points,payload,updated_at) VALUES(?,?,?,?,?,?,?,CURRENT_TIMESTAMP)",[id,club,season,week,budget,points,payload])

func recordMatch(season:int,round_no:int,home:String,away:String,hg:int,ag:int,points:int) -> void:
	if ready: db.query_with_bindings("INSERT INTO matches(season,round_no,home,away,home_goals,away_goals,points_after) VALUES(?,?,?,?,?,?,?)",[season,round_no,home,away,hg,ag,points])

func recordTransfer(club:String,player:String,direction:String,fee:int,week:int) -> void:
	if ready: db.query_with_bindings("INSERT INTO transfers(club,player,direction,fee,week) VALUES(?,?,?,?,?)",[club,player,direction,fee,week])

func recordScout(region:String,report:String,week:int) -> void:
	if ready: db.query_with_bindings("INSERT INTO scouting_reports(region,report,week) VALUES(?,?,?)",[region,report,week])

func recordCompetition(name:String,country:String) -> void:
	if ready: db.query_with_bindings("INSERT OR IGNORE INTO competitions(name,country) VALUES(?,?)",[name,country])

func recordClub(name:String,competition:String,rating:int) -> void:
	if ready: db.query_with_bindings("INSERT OR IGNORE INTO clubs(name,competition,rating) VALUES(?,?,?)",[name,competition,rating])

func replacePlayers(rows:Array) -> void:
	if not ready: return
	db.query("DELETE FROM players")
	for p in rows:
		db.query_with_bindings("INSERT INTO players(name,club,position,age,overall,potential,value,wage) VALUES(?,?,?,?,?,?,?,?)",[p.get("name",""),p.get("club",""),p.get("position",""),p.get("age",18),p.get("overall",50),p.get("potential",60),p.get("value",0),p.get("wage",0)])

func _exit_tree() -> void:
	if db and ready: db.close_db()
