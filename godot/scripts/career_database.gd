extends Node
## Local structured career database implemented with Godot's built-in JSON/FileAccess APIs.
## This keeps the project dependency-free; it can be migrated to SQLite later.

const DB_PATH := "user://football_empire_database.json"
var data: Dictionary = {
	"career_saves": [],
	"matches": [],
	"transfers": [],
	"scouting_reports": [],
	"clubs": [],
	"competitions": [],
	"players": [],
	"fixtures": [],
	"training": [],
	"club_news": []
}

func _ready() -> void:
	load_database()

func load_database() -> void:
	if not FileAccess.file_exists(DB_PATH):
		save_database()
		return
	var file := FileAccess.open(DB_PATH, FileAccess.READ)
	if file == null:
		return
	var parsed = JSON.parse_string(file.get_as_text())
	if typeof(parsed) == TYPE_DICTIONARY:
		for key in data.keys():
			if parsed.has(key) and typeof(parsed[key]) == TYPE_ARRAY:
				data[key] = parsed[key]

func save_database() -> void:
	var file := FileAccess.open(DB_PATH, FileAccess.WRITE)
	if file:
		file.store_string(JSON.stringify(data))

func saveCareer(id: int, club: String, season: int, week: int, budget: int, points: int, payload: String) -> void:
	var row := {"id": id, "club": club, "season": season, "week": week, "budget": budget, "points": points, "payload": payload}
	_upsert("career_saves", "id", row)

func recordMatch(season: int, round_no: int, home: String, away: String, hg: int, ag: int, points: int) -> void:
	data.matches.append({"season":season,"round":round_no,"home":home,"away":away,"home_goals":hg,"away_goals":ag,"points_after":points})
	save_database()

func recordTransfer(club: String, player: String, direction: String, fee: int, week: int) -> void:
	data.transfers.append({"club":club,"player":player,"direction":direction,"fee":fee,"week":week})
	save_database()

func recordScout(region: String, report: String, week: int) -> void:
	data.scouting_reports.append({"region":region,"report":report,"week":week})
	save_database()

func recordCompetition(name: String, country: String) -> void:
	for item in data.competitions:
		if item.get("name","") == name:
			return
	data.competitions.append({"name":name,"country":country})
	save_database()

func recordClub(name: String, competition: String, rating: int) -> void:
	for item in data.clubs:
		if item.get("name","") == name:
			return
	data.clubs.append({"name":name,"competition":competition,"rating":rating})
	save_database()

func replacePlayers(rows: Array) -> void:
	data.players = rows.duplicate(true)
	save_database()

func _upsert(table: String, key: String, row: Dictionary) -> void:
	for i in range(data[table].size()):
		if str(data[table][i].get(key,"")) == str(row.get(key,"")):
			data[table][i] = row
			save_database()
			return
	data[table].append(row)
	save_database()
