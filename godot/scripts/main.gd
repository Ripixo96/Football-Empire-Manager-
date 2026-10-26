extends Control

const SAVE_PATH := "user://football_empire_save.json"
const CLUBS := [
	{"name":"Real Madrid","league":"Hiszpania • LaLiga","rating":88,"budget":125000000,"color":Color("#f4f0df")},
	{"name":"FC Barcelona","league":"Hiszpania • LaLiga","rating":86,"budget":90000000,"color":Color("#163b87")},
	{"name":"Manchester City","league":"Anglia • Premier League","rating":89,"budget":140000000,"color":Color("#6cbbdf")},
	{"name":"Liverpool","league":"Anglia • Premier League","rating":86,"budget":95000000,"color":Color("#c8102e")},
	{"name":"Arsenal","league":"Anglia • Premier League","rating":84,"budget":85000000,"color":Color("#ef0107")},
	{"name":"Bayern Monachium","league":"Niemcy • Bundesliga","rating":87,"budget":110000000,"color":Color("#dc052d")},
	{"name":"Paris Saint-Germain","league":"Francja • Ligue 1","rating":85,"budget":100000000,"color":Color("#004170")},
	{"name":"Inter Mediolan","league":"Włochy • Serie A","rating":84,"budget":75000000,"color":Color("#00529f")},
	{"name":"Juventus","league":"Włochy • Serie A","rating":82,"budget":65000000,"color":Color("#eeeeee")},
	{"name":"Ajax","league":"Holandia • Eredivisie","rating":77,"budget":28000000,"color":Color("#d2122e")},
	{"name":"Benfica","league":"Portugalia • Liga Portugal","rating":79,"budget":35000000,"color":Color("#e32219")},
	{"name":"Legia Warszawa","league":"Polska • Ekstraklasa","rating":70,"budget":8500000,"color":Color("#166b45")},
	{"name":"Lech Poznań","league":"Polska • Ekstraklasa","rating":69,"budget":7000000,"color":Color("#2674c8")},
	{"name":"Celtic","league":"Szkocja • Premiership","rating":74,"budget":18000000,"color":Color("#168447")},
	{"name":"Boca Juniors","league":"Argentyna • Primera División","rating":77,"budget":22000000,"color":Color("#163b8b")}
]
const FIRST_NAMES := ["Adam","Mateusz","Jan","Kacper","Michał","Jakub","Antoni","Leon","Filip","Oskar","Dawid","Karol","Noah","Lucas","Marco","Daniel","Alex","Samuel","Hugo","Rafael"]
const LAST_NAMES := ["Kowalski","Nowak","Zieliński","Lewandowski","Wójcik","Kamiński","Silva","Santos","Garcia","Rossi","Muller","Costa","Fernandes","Novak","Kane","Walker","Martin","Diaz","Petrov","Andersen"]
const POSITIONS := ["BR","PO","ŚO","ŚO","LO","ŚPD","ŚP","ŚP","PP","N","LP","ŚO","ŚP","N","PO","LO","PP","ŚPD","N","BR"]
const SAMPLE_MARKET := [
	{"name":"Luca Moretti","pos":"N","age":21,"ovr":76,"value":18000000},
	{"name":"Milan Petrovic","pos":"ŚP","age":19,"ovr":71,"value":8500000},
	{"name":"Ethan Clarke","pos":"PO","age":24,"ovr":78,"value":24000000},
	{"name":"Rafael Costa","pos":"LP","age":20,"ovr":74,"value":14500000},
	{"name":"Noah Andersen","pos":"ŚO","age":22,"ovr":75,"value":16000000},
	{"name":"Daniel Novak","pos":"BR","age":25,"ovr":73,"value":9500000}
]

var career_started := false
var club: Dictionary = {}
var players: Array[Dictionary] = []
var market: Array[Dictionary] = []
var fixtures: Array[Dictionary] = []
var results: Array[Dictionary] = []
var news: Array[String] = ["Witamy w Football Empire. Wybierz klub i rozpocznij karierę."]
var formation := "4-3-3"
var tactics := "Zrównoważona"
var morale := 75
var season := 2026
var week := 1
var budget := 0
var points := 0
var played := 0
var wins := 0
var draws := 0
var losses := 0
var goals_for := 0
var goals_against := 0
var training_focus := "Technika"
var page := "home"
var match_running := false
var match_time := 0.0
var match_home_goals := 0
var match_away_goals := 0
var match_events: Array[String] = []
var ball_pos := Vector2(0.5, 0.5)
var ball_target := Vector2(0.5, 0.5)
var rng := RandomNumberGenerator.new()
var root: VBoxContainer
var body: VBoxContainer
var header_label: Label
var footer_label: Label

func _ready() -> void:
	rng.randomize()
	careerDb = preload("res://scripts/career_database.gd").new()
	careerDb.name = "CareerDatabase"
	add_child(careerDb)
	_build_shell()
	if not _load_career():
		show_club_select()
	else:
		show_home()

func _build_shell() -> void:
	var bg := ColorRect.new()
	bg.color = Color("#09111f")
	bg.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	add_child(bg)
	root = VBoxContainer.new()
	root.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	root.add_theme_constant_override("separation", 8)
	root.add_theme_constant_override("margin_left", 16)
	root.add_theme_constant_override("margin_right", 16)
	root.add_theme_constant_override("margin_top", 12)
	root.add_theme_constant_override("margin_bottom", 12)
	add_child(root)
	var top := HBoxContainer.new()
	root.add_child(top)
	var brand := Label.new()
	brand.text = "⚽ FOOTBALL EMPIRE"
	brand.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	brand.add_theme_font_size_override("font_size", 20)
	brand.add_theme_color_override("font_color", Color("#66e3b4"))
	top.add_child(brand)
	header_label = Label.new()
	header_label.text = "KARIERA"
	header_label.add_theme_color_override("font_color", Color("#a9b8ce"))
	top.add_child(header_label)
	var line := ColorRect.new()
	line.custom_minimum_size.y = 2
	line.color = Color("#26364d")
	root.add_child(line)
	var scroll := ScrollContainer.new()
	scroll.size_flags_vertical = Control.SIZE_EXPAND_FILL
	scroll.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	root.add_child(scroll)
	body = VBoxContainer.new()
	body.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	body.add_theme_constant_override("separation", 10)
	scroll.add_child(body)
	footer_label = Label.new()
	footer_label.text = "KARIERA • TAKTYKA • MECZE • TRANSFERY"
	footer_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	footer_label.add_theme_color_override("font_color", Color("#71839d"))
	root.add_child(footer_label)

func _clear_body(title: String) -> void:
	for child in body.get_children():
		child.queue_free()
	header_label.text = title.to_upper()

func _label(text: String, size: int = 15, color: Color = Color("#edf4ff")) -> Label:
	var l := Label.new()
	l.text = text
	l.add_theme_font_size_override("font_size", size)
	l.add_theme_color_override("font_color", color)
	l.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	body.add_child(l)
	return l

func _button(text: String, callback: Callable, accent: bool = false) -> Button:
	var b := Button.new()
	b.text = text
	b.custom_minimum_size.y = 48
	b.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	b.add_theme_font_size_override("font_size", 15)
	var style := StyleBoxFlat.new()
	style.bg_color = Color("#147c62") if accent else Color("#17253a")
	style.set_corner_radius_all(8)
	style.content_margin_left = 12
	style.content_margin_right = 12
	style.content_margin_top = 8
	style.content_margin_bottom = 8
	b.add_theme_stylebox_override("normal", style)
	var hover := style.duplicate()
	hover.bg_color = Color("#1a9878") if accent else Color("#253954")
	b.add_theme_stylebox_override("hover", hover)
	b.pressed.connect(callback)
	body.add_child(b)
	return b

func _card(text: String) -> void:
	var panel := PanelContainer.new()
	var style := StyleBoxFlat.new()
	style.bg_color = Color("#111e30")
	style.set_corner_radius_all(10)
	style.content_margin_left = 12
	style.content_margin_right = 12
	style.content_margin_top = 10
	style.content_margin_bottom = 10
	panel.add_theme_stylebox_override("panel", style)
	body.add_child(panel)
	var l := Label.new()
	l.text = text
	l.add_theme_font_size_override("font_size", 14)
	l.add_theme_color_override("font_color", Color("#e7eef8"))
	l.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	panel.add_child(l)

func _two_buttons(a: String, ca: Callable, b: String, cb: Callable) -> void:
	var row := HBoxContainer.new()
	row.add_theme_constant_override("separation", 8)
	body.add_child(row)
	for data in [[a,ca],[b,cb]]:
		var btn := Button.new()
		btn.text = data[0]
		btn.custom_minimum_size.y = 44
		btn.size_flags_horizontal = Control.SIZE_EXPAND_FILL
		btn.add_theme_font_size_override("font_size", 13)
		var style := StyleBoxFlat.new()
		style.bg_color = Color("#17253a")
		style.set_corner_radius_all(7)
		btn.add_theme_stylebox_override("normal",style)
		btn.pressed.connect(data[1])
		row.add_child(btn)

func show_club_select() -> void:
	_clear_body("Nowa kariera")
	_label("ZBUDUJ SWOJĄ DYNASTIĘ", 24, Color("#66e3b4"))
	_label("Wybierz klub. Każdy zaczyna z innym budżetem i poziomem sportowym.")
	for c in CLUBS:
		var row := Button.new()
		row.text = "%s\n%s  •  Siła %d  •  Budżet %s" % [c.name,c.league,c.rating,_money(c.budget)]
		row.custom_minimum_size.y = 76
		row.size_flags_horizontal = Control.SIZE_EXPAND_FILL
		row.add_theme_font_size_override("font_size", 14)
		var style := StyleBoxFlat.new()
		style.bg_color = Color("#132239")
		style.set_corner_radius_all(8)
		style.content_margin_left = 12
		style.content_margin_right = 12
		row.add_theme_stylebox_override("normal",style)
		row.pressed.connect(func(): _start_career(c))
		body.add_child(row)
	_button("Wczytaj zapis kariery", _load_and_open)

func _start_career(selected: Dictionary) -> void:
	club = selected.duplicate(true)
	budget = int(club.budget)
	players.clear()
	for i in range(20):
		var pos := str(POSITIONS[i])
		var age := rng.randi_range(18,32)
		var ovr := clampi(int(club.rating) - rng.randi_range(-8,10), 52, 91)
		var potential := clampi(ovr + rng.randi_range(2,16),ovr,94)
		players.append({"id":i+1,"name":FIRST_NAMES[rng.randi_range(0,FIRST_NAMES.size()-1)]+" "+LAST_NAMES[rng.randi_range(0,LAST_NAMES.size()-1)],"pos":pos,"age":age,"ovr":ovr,"potential":potential,"value":_player_value(ovr,age),"wage":ovr*700,"morale":rng.randi_range(62,91),"fitness":100,"contract":rng.randi_range(1,5)})
	market.clear()
	for p in SAMPLE_MARKET:
		market.append(p.duplicate(true))
	fixtures.clear()
	results.clear()
	season = 2026
	week = 1
	played = 0
	points = 0
	wins = 0
	draws = 0
	losses = 0
	goals_for = 0
	goals_against = 0
	morale = 75
	formation = "4-3-3"
	tactics = "Zrównoważona"
	career_started = true
	news = ["%s ogłasza nowego menedżera!" % club.name, "Zarząd wyznaczył cel: rozwój klubu i walka o ligowe punkty."]
	_generate_fixtures()
	_save_career()
	show_home()

func _generate_fixtures() -> void:
	fixtures.clear()
	var opponents := ["Northbridge FC","Royal Athletic","Capital United","Sporting Albion","Dynamo City","Olympique Nord","Red Star Town","Blue Coast FC","Union Sporting","Racing Metro","Kingsford","Valencia Norte","AC Lombardia","Bavaria 04","Porto Azul","Warsaw Athletic","Rotterdam 1908","Lisbon Stars","Glasgow Green","Istanbul SK"]
	opponents.shuffle()
	for i in range(20):
		fixtures.append({"week":i+1,"opponent":opponents[i],"home":i%2==0,"played":false,"score":"—"})

func show_home() -> void:
	_clear_body("Centrum kariery")
	if not career_started:
		show_club_select()
		return
	_label(str(club.name),25,Color("#66e3b4"))
	_label("%s  •  Sezon %d/%d  •  Kolejka %d" % [club.league,season,season+1,week])
	var played_text := "%d pkt  |  %d meczów  |  %d-%d-%d" % [points,played,wins,draws,losses]
	_card(played_text)
	_card("Budżet transferowy\n%s" % _money(budget))
	_card("Morale zespołu  %d/100     Ocena zarządu  %d/100" % [morale,clampi(50+points*2+ wins*2-losses*2,0,100)])
	_label("NAJBLIŻSZE SPOTKANIE",18,Color("#66e3b4"))
	var next := _next_fixture()
	if next.is_empty():
		_card("Sezon zakończony. Rozpocznij nowy sezon w kalendarzu.")
	else:
		_card("%s  %s  %s" % [club.name,"VS" if next.home else "@",next.opponent])
		_button("ROZEGRAJ MECZ", show_match_preview, true)
	_label("ZARZĄDZANIE KLUBEM",17)
	_two_buttons("Skład",show_squad,"Taktyka",show_tactics)
	_two_buttons("Transfery",show_transfers,"Trening",show_development)
	_two_buttons("Skauting",show_scouting,"Kalendarz",show_calendar)
	_two_buttons("Wiadomości",show_news,"Zapis kariery",show_saves)
	_two_buttons("Tabela ligowa",show_table,"Baza danych",show_database)
	_button("Zapisz karierę", func(): _save_career(); _toast("Kariera zapisana"), true)

func _next_fixture() -> Dictionary:
	for f in fixtures:
		if not f.played:
			return f
	return {}

func show_squad() -> void:
	_clear_body("Pierwszy zespół")
	_label("%s  •  %d zawodników" % [club.name,players.size()],21,Color("#66e3b4"))
	var sorted := players.duplicate()
	sorted.sort_custom(func(a,b): return int(a.ovr)>int(b.ovr))
	for p in sorted:
		_card("%s   %s   OVR %d / POT %d\nWiek %d  •  Wartość %s  •  Kontrakt %d l.\nForma %d%%  •  Morale %d" % [p.name,p.pos,p.ovr,p.potential,p.age,_money(p.value),p.contract,p.fitness,p.morale])
		_two_buttons("Trenuj: "+p.name,func(): _train_player(int(p.id)),"Szczegóły",func(): _player_detail(p))
	_button("Wróć do kariery",show_home)

func _player_detail(p: Dictionary) -> void:
	_clear_body("Profil zawodnika")
	_label(str(p.name),24,Color("#66e3b4"))
	_card("Pozycja: %s\nOcena: %d  •  Potencjał: %d\nWiek: %d\nWartość rynkowa: %s\nPensja tygodniowa: %s\nKontrakt: %d lat\nMorale: %d/100\nKondycja: %d/100" % [p.pos,p.ovr,p.potential,p.age,_money(p.value),_money(p.wage),p.contract,p.morale,p.fitness])
	_button("Wystaw na listę transferową",func(): _list_player_for_sale(p),true)
	_button("Wróć do składu",show_squad)

func show_tactics() -> void:
	_clear_body("Taktyka i ustawienie")
	_label("FORMACJA",18,Color("#66e3b4"))
	for f in ["4-3-3","4-2-3-1","4-4-2","3-5-2","5-3-2"]:
		_button(("✓ " if formation==f else "")+f,func(): formation=f; _save_career(); show_tactics(),formation==f)
	_label("STYL GRY",18,Color("#66e3b4"))
	for t in ["Zrównoważona","Wysoki pressing","Kontratak","Posiadanie piłki","Głęboka obrona"]:
		_button(("✓ " if tactics==t else "")+t,func(): tactics=t; _save_career(); show_tactics(),tactics==t)
	_card("Ustawienie i styl wpływają na szanse w symulacji meczu. Pressing zwiększa tempo, ale szybciej męczy piłkarzy.")
	_button("Wróć",show_home)

func show_match_preview() -> void:
	var f := _next_fixture()
	if f.is_empty():
		show_home()
		return
	_clear_body("Centrum meczowe")
	_label("DZIEŃ MECZOWY",23,Color("#66e3b4"))
	_card("%s  %s  %s" % [club.name,"VS" if f.home else "@",f.opponent])
	_card("Ustawienie: %s\nStyl: %s\nMorale: %d/100\nDostępny skład: %d" % [formation,tactics,morale,players.size()])
	_button("SYMULUJ MECZ",_simulate_match,true)
	_button("OGLĄDAJ MECZ 2D",_start_2d_match,true)
	_button("Wróć do kariery",show_home)

func _simulate_match() -> void:
	var f := _next_fixture()
	if f.is_empty(): return
	var strength := _team_strength()
	var opponent_strength := int(club.rating)-rng.randi_range(-5,7)
	var home_adv := 1.0 if f.home else 0.0
	var attack_bonus := 0.0
	if tactics=="Wysoki pressing": attack_bonus=0.25
	elif tactics=="Kontratak": attack_bonus=0.12
	elif tactics=="Posiadanie piłki": attack_bonus=0.1
	var hg := clampi(int(round(rng.randfn(1.25+(strength-opponent_strength)*0.065+home_adv*0.35+attack_bonus,0.95))),0,6)
	var ag := clampi(int(round(rng.randfn(1.05+(opponent_strength-strength)*0.055-home_adv*0.2,0.85))),0,5)
	_finish_match(f,hg,ag)
	_show_result(hg,ag,str(f.opponent))

func _finish_match(f: Dictionary,hg: int,ag: int) -> void:
	f.played=true
	f.score="%d-%d" % ([hg,ag] if f.home else [ag,hg])
	played+=1
	goals_for+=hg
	goals_against+=ag
	if hg>ag: wins+=1; points+=3; morale=mini(100,morale+5)
	elif hg==ag: draws+=1; points+=1; morale=mini(100,morale+1)
	else: losses+=1; morale=maxi(20,morale-5)
	for p in players:
		p.fitness=maxi(45,int(p.fitness)-rng.randi_range(8,17))
		if p.ovr<p.potential and rng.randf()<0.08: p.ovr+=1
	week+=1
	seasonDay+=7
	news.push_front("%s %d:%d %s — kolejka %d" % [club.name,hg,ag,f.opponent,played])
	if careerDb: careerDb.recordMatch(season,played,club.name,str(f.opponent),hg,ag,points)
	_save_career()

func _show_result(hg: int,ag: int,opponent: String) -> void:
	_clear_body("Raport meczowy")
	_label("KONIEC MECZU",16,Color("#66e3b4"))
	_label("%s  %d : %d  %s" % [club.name,hg,ag,opponent],24)
	_card("Posiadanie piłki: %d%% - %d%%\nStrzały: %d - %d\nCelne: %d - %d\nPodania: %d - %d" % [rng.randi_range(42,64),rng.randi_range(36,58),rng.randi_range(7,18),rng.randi_range(5,15),rng.randi_range(2,9),rng.randi_range(1,7),rng.randi_range(250,550),rng.randi_range(220,480)])
	_label("Wydarzenia",18,Color("#66e3b4"))
	for i in range(hg+ag):
		_card("%d'  GOL — %s" % [rng.randi_range(4,89),players[rng.randi_range(0,players.size()-1)].name if rng.randf()<0.7 else opponent])
	_button("Kontynuuj karierę",show_home,true)

func _start_2d_match() -> void:
	var f := _next_fixture()
	if f.is_empty(): return
	match_running=true
	match_time=0
	match_home_goals=0
	match_away_goals=0
	match_events.clear()
	ball_pos=Vector2(0.5,0.5)
	ball_target=Vector2(0.5,0.5)
	_clear_body("Mecz 2D")
	var pitch := MatchPitch.new()
	pitch.custom_minimum_size=Vector2(300,330)
	pitch.size_flags_horizontal=Control.SIZE_EXPAND_FILL
	pitch.set_match_data(players,club.name,str(f.opponent),formation)
	body.add_child(pitch)
	pitch.goal_event.connect(func(home_goal: bool): 
		if home_goal: match_home_goals+=1
		else: match_away_goals+=1
		match_events.append("%d' GOL!" % int(match_time))
	)
	var score := Label.new()
	score.name="MatchScore"
	score.text="%s  0 : 0  %s\nMinuta 0 / 90" % [club.name,f.opponent]
	score.horizontal_alignment=HORIZONTAL_ALIGNMENT_CENTER
	score.add_theme_font_size_override("font_size",20)
	body.add_child(score)
	var timer := Timer.new()
	timer.wait_time=0.7
	timer.timeout.connect(func():
		if not is_instance_valid(pitch): timer.stop(); return
		match_time+=3.0
		score.text="%s  %d : %d  %s\nMinuta %d / 90" % [club.name,match_home_goals,match_away_goals,f.opponent,mini(90,int(match_time))]
		if match_time>=90:
			timer.stop()
			match_running=false
			_finish_match(f,match_home_goals,match_away_goals)
			_show_result(match_home_goals,match_away_goals,str(f.opponent))
	)
	add_child(timer)
	timer.start()
	_button("Przerwij i zasymuluj resztę",func():
		timer.stop()
		match_running=false
		var hg=match_home_goals
		var ag=match_away_goals
		_finish_match(f,hg,ag)
		_show_result(hg,ag,str(f.opponent))
	)

class MatchPitch extends Control:
	signal goal_event(home_goal: bool)
	var squad: Array = []
	var home_name := "Home"
	var away_name := "Away"
	var formation_name := "4-3-3"
	var elapsed := 0.0
	var next_event := 5.0
	var home_score := 0
	var away_score := 0
	var ball := Vector2(0.5,0.5)
	var target := Vector2(0.5,0.5)
	func set_match_data(p:Array, h:String, a:String, f:String) -> void:
		squad=p; home_name=h; away_name=a; formation_name=f
	func _process(delta:float) -> void:
		elapsed+=delta
		ball=ball.move_toward(target,delta*0.12)
		if elapsed>=next_event:
			elapsed=0
			next_event=randf_range(4.0,11.0)
			if randf()<0.12:
				var home_goal=randf()<0.52
				if home_goal: home_score+=1
				else: away_score+=1
				goal_event.emit(home_goal)
			target=Vector2(randf_range(0.08,0.92),randf_range(0.08,0.92))
		queue_redraw()
	func _draw() -> void:
		var r=Rect2(Vector2(4,4),size-Vector2(8,8))
		draw_rect(r,Color("#167449"))
		var stripe_w=r.size.x/10
		for i in range(10):
			if i%2==0: draw_rect(Rect2(r.position+Vector2(i*stripe_w,0),Vector2(stripe_w,r.size.y)),Color("#1c8253"))
		var white=Color("#e9f7ef")
		draw_rect(Rect2(r.position+Vector2(r.size.x*0.05,r.size.y*0.05),r.size*0.9),white,false,2)
		draw_line(Vector2(size.x/2,4),Vector2(size.x/2,size.y-4),white,2)
		draw_arc(size/2,min(size.x,size.y)*0.1,0,TAU,64,white,2)
		draw_rect(Rect2(Vector2(size.x*0.05,size.y*0.30),Vector2(size.x*0.13,size.y*0.40)),white,false,2)
		draw_rect(Rect2(Vector2(size.x*0.82,size.y*0.30),Vector2(size.x*0.13,size.y*0.40)),white,false,2)
		for i in range(11):
			var x=0.12+float(i%4)*0.24
			var y=0.15+float(i/4)*0.28
			draw_circle(Vector2(x*size.x,y*size.y),7,Color("#d9f0ff"))
			draw_circle(Vector2((1.0-x)*size.x,(1.0-y)*size.y),7,Color("#ffdf79"))
		draw_circle(Vector2(ball.x*size.x,ball.y*size.y),5,Color.WHITE)
		draw_string(ThemeDB.fallback_font,Vector2(12,22),home_name, HORIZONTAL_ALIGNMENT_LEFT,-1,12,white)
		draw_string(ThemeDB.fallback_font,Vector2(size.x-12,22),away_name,HORIZONTAL_ALIGNMENT_RIGHT,-1,12,white)

func show_transfers() -> void:
	_clear_body("Rynek transferowy")
	_label("OKNO TRANSFEROWE",20,Color("#66e3b4"))
	_card("Budżet: %s\nProwizje i pensje są uwzględniane w finansach. Lista kandydatów to obecnie lokalna baza demonstracyjna; rzeczywiste składy światowe będą osobnym importem danych." % _money(budget))
	for p in market:
		_card("%s  •  %s  •  OVR %d\nWiek %d  •  Wartość %s" % [p.name,p.pos,p.ovr,p.age,_money(p.value)])
		_button("Złóż ofertę",func(): _buy_player(p),true)
	_button("Sprzedaj zawodnika",show_sell_player)
	_button("Wróć",show_home)

func _buy_player(p: Dictionary) -> void:
	if budget<int(p.value):
		_toast("Za mały budżet")
		return
	budget-=int(p.value)
	players.append({"id":_next_player_id(),"name":p.name,"pos":p.pos,"age":p.age,"ovr":p.ovr,"potential":min(94,int(p.ovr)+rng.randi_range(3,15)),"value":p.value,"wage":int(p.ovr)*700,"morale":75,"fitness":100,"contract":4})
	market.erase(p)
	news.push_front("Klub pozyskał zawodnika: "+str(p.name))
	if careerDb: careerDb.recordTransfer(club.name,str(p.name),"IN",int(p.value),week)
	_save_career()
	show_transfers()

func show_sell_player() -> void:
	_clear_body("Lista transferowa")
	for p in players:
		_button("%s (%s, OVR %d) — %s" % [p.name,p.pos,p.ovr,_money(p.value)],func(): _sell_player(p))
	_button("Wróć",show_transfers)

func _list_player_for_sale(p: Dictionary) -> void:
	market.append({"name":p.name,"pos":p.pos,"age":p.age,"ovr":p.ovr,"value":int(p.value)})
	news.push_front(str(p.name)+" wystawiony na sprzedaż")
	show_transfers()

func _sell_player(p: Dictionary) -> void:
	var fee=int(int(p.value)*rng.randf_range(0.85,1.2))
	budget+=fee
	players.erase(p)
	news.push_front("Sprzedano %s za %s" % [p.name,_money(fee)])
	if careerDb: careerDb.recordTransfer(club.name,str(p.name),"OUT",fee,week)
	_save_career()
	show_sell_player()

func show_development() -> void:
	_clear_body("Rozwój i trening")
	_card("Poziom treningu: %d\nTrening poprawia oceny, jeśli zawodnik nie osiągnął potencjału." % trainingLevel)
	for focus in ["Technika","Strzały","Podania","Obrona","Kondycja"]:
		_button("Trening: "+focus,func(): training_focus=focus; _train_team())
	_label("Wybierz zawodnika do indywidualnego treningu",17,Color("#66e3b4"))
	for p in players:
		_button("%s • %s • %d/%d" % [p.name,p.pos,p.ovr,p.potential],func(): _train_player(int(p.id)))
	_button("Wróć",show_home)

func _train_team() -> void:
	var cost=150000+trainingLevel*50000
	if budget<cost: _toast("Za mało pieniędzy na trening"); return
	budget-=cost
	trainingLevel+=1
	for p in players:
		p.fitness=maxi(55,int(p.fitness)-4)
		if int(p.ovr)<int(p.potential) and rng.randf()<0.25: p.ovr+=1
	news.push_front("Zakończono trening zespołowy: "+training_focus)
	_save_career()
	show_development()

func _train_player(id:int) -> void:
	for p in players:
		if int(p.id)==id:
			if budget<50000: _toast("Za mało pieniędzy"); return
			budget-=50000
			if int(p.ovr)<int(p.potential): p.ovr+=1
			p.fitness=maxi(50,int(p.fitness)-5)
			p.value=_player_value(int(p.ovr),int(p.age))
			news.push_front("Indywidualny trening: "+str(p.name))
			break
	_save_career()
	show_development()

func show_scouting() -> void:
	_clear_body("Skauting")
	_label("SIEĆ SKAUTÓW",22,Color("#66e3b4"))
	_card("Skautowanie kosztuje i tworzy raport o potencjale. W tej wersji raporty są generowane przez symulację, nie przez zweryfikowaną bazę wszystkich zawodników świata.")
	for region in ["Europa","Ameryka Południowa","Afryka","Azja","Ameryka Północna"]:
		_button("Wyślij skauta: "+region,func(): _scout_region(region),true)
	_button("Wróć",show_home)

func _scout_region(region:String) -> void:
	if budget<250000: _toast("Skauting kosztuje 250 tys."); return
	budget-=250000
	var report="%s: %s, %s, wiek %d, OVR %d, potencjał %d, wartość %s" % [region,FIRST_NAMES[rng.randi_range(0,FIRST_NAMES.size()-1)]+" "+LAST_NAMES[rng.randi_range(0,LAST_NAMES.size()-1)],POSITIONS[rng.randi_range(0,POSITIONS.size()-1)],rng.randi_range(16,23),rng.randi_range(55,76),rng.randi_range(75,94),_money(rng.randi_range(500000,12000000))]
	news.push_front("Raport skautingowy — "+report)
	if careerDb: careerDb.recordScout(region,report,week)
	_save_career()
	_clear_body("Raport skautingowy")
	_label("RAPORT GOTOWY",22,Color("#66e3b4"))
	_card(report)
	_button("Rynek transferowy",show_transfers)
	_button("Wróć",show_home)

func showMorale() -> void:
	_clear_body("Morale")
	_card("Morale zespołu: %d/100" % morale)
	_button("Spotkanie motywacyjne (100 tys.)",func():
		if budget>=100000: budget-=100000; morale=mini(100,morale+10); news.push_front("Spotkanie motywacyjne poprawiło atmosferę."); _save_career(); showMorale()
		else: _toast("Za mało środków"))
	_button("Dzień regeneracji",func():
		morale=mini(100,morale+4)
		for p in players: p.fitness=mini(100,int(p.fitness)+12)
		_save_career(); showMorale())
	_button("Wróć",show_home)

func showNews() -> void:
	_clear_body("Wiadomości sportowe")
	for n in news:
		_card("• "+n)
	_button("Wróć",show_home)

func showCalendar() -> void:
	_clear_body("Kalendarz sezonu")
	_label("Sezon %d/%d" % [season,season+1],21,Color("#66e3b4"))
	for f in fixtures:
		_card("Kolejka %02d  •  %s %s %s  •  %s" % [f.week,club.name,"vs" if f.home else "@",f.opponent,f.score if f.played else "NADCHODZĄCY"])
	if played>=fixtures.size():
		_button("Rozpocznij nowy sezon",func():
			season+=1; week=1; played=0; points=0; wins=0; draws=0; losses=0; goals_for=0; goals_against=0
			for p in players: p.age+=1; p.contract=maxi(0,int(p.contract)-1); p.fitness=100
			_generate_fixtures(); _save_career(); show_home(),true)
	_button("Wróć",show_home)

func show_table() -> void:
	_clear_body("Tabela ligowa")
	_card("1. %s — %d pkt" % [club.name,points])
	for i in range(1,10):
		_card("%d. %s — %d pkt" % [i+1,["Northbridge FC","Royal Athletic","Capital United","Sporting Albion","Dynamo City","Olympique Nord","Red Star Town","Blue Coast FC","Union Sporting"][i-1],rng.randi_range(0,played*3)])
	_button("Wróć",show_home)

func show_database() -> void:
	_clear_body("Baza danych")
	_card("Lokalna baza kariery: %d zawodników, %d spotkań rozegranych, %d wiadomości." % [players.size(),played,news.size()])
	_card("SQLite zapisuje mecze, transfery, skauting, kluby i rozgrywki. Dane światowych lig i składów wymagają osobnego, legalnego źródła i importu; nie udajemy, że przykładowa lista to pełna baza.")
	_button("Eksportuj zapis JSON",func(): _save_career(); _toast("Zapis JSON odświeżony"))
	_button("Wróć",show_home)

func show_saves() -> void:
	_clear_body("Zapis kariery")
	_label("ZAPIS LOKALNY",20,Color("#66e3b4"))
	_card("Automatyczny zapis po meczach i transferach. Zapis jest przechowywany w pamięci aplikacji.")
	_button("Zapisz teraz",func(): _save_career(); _toast("Zapisano karierę"),true)
	_button("Wczytaj zapis",_load_and_open)
	_button("Nowa kariera",show_club_select)
	_button("Wróć",show_home)

func _save_career() -> void:
	if not career_started: return
	var data={"club":club,"players":players,"market":market,"fixtures":fixtures,"results":results,"news":news,"formation":formation,"tactics":tactics,"morale":morale,"season":season,"week":week,"budget":budget,"points":points,"played":played,"wins":wins,"draws":draws,"losses":losses,"goals_for":goals_for,"goals_against":goals_against,"trainingLevel":trainingLevel,"career_started":career_started}
	var file=FileAccess.open(SAVE_PATH,FileAccess.WRITE)
	if file: file.store_string(JSON.stringify(data))
	if careerDb: careerDb.saveCareer(1,club.name,season,week,budget,points,JSON.stringify(data))

func _load_career() -> bool:
	if not FileAccess.file_exists(SAVE_PATH): return false
	var file=FileAccess.open(SAVE_PATH,FileAccess.READ)
	if file==null: return false
	var parsed=JSON.parse_string(file.get_as_text())
	if typeof(parsed)!=TYPE_DICTIONARY or not parsed.get("career_started",false): return false
	club=parsed.get("club",{})
	players=parsed.get("players",[])
	market=parsed.get("market",[])
	fixtures=parsed.get("fixtures",[])
	results=parsed.get("results",[])
	news=parsed.get("news",[])
	formation=parsed.get("formation","4-3-3")
	tactics=parsed.get("tactics","Zrównoważona")
	morale=int(parsed.get("morale",75))
	season=int(parsed.get("season",2026))
	week=int(parsed.get("week",1))
	budget=int(parsed.get("budget",0))
	points=int(parsed.get("points",0))
	played=int(parsed.get("played",0))
	wins=int(parsed.get("wins",0))
	draws=int(parsed.get("draws",0))
	losses=int(parsed.get("losses",0))
	goals_for=int(parsed.get("goals_for",0))
	goals_against=int(parsed.get("goals_against",0))
	trainingLevel=int(parsed.get("trainingLevel",1))
	career_started=true
	return true

func _load_and_open() -> void:
	if _load_career(): show_home()
	else: show_club_select()

func _team_strength() -> float:
	if players.is_empty(): return 60.0
	var total:=0.0
	for p in players: total+=float(p.ovr)*float(p.fitness)/100.0
	return total/players.size()+float(morale-70)*0.04

func _player_value(ovr:int,age:int) -> int:
	var value=int(pow(maxf(1.0,float(ovr-40)),2.45)*1200.0)
	if age<22: value=int(value*1.35)
	elif age>30: value=int(value*0.65)
	return maxi(50000,value)

func _next_player_id() -> int:
	var max_id=0
	for p in players: max_id=maxi(max_id,int(p.id))
	return max_id+1

func _money(amount:int) -> String:
	if amount>=1000000: return "%.1f mln €" % (float(amount)/1000000.0)
	return "%d tys. €" % int(amount/1000)

func _toast(message:String) -> void:
	footer_label.text=message
	var timer=get_tree().create_timer(2.5)
	timer.timeout.connect(func(): if is_instance_valid(footer_label): footer_label.text="KARIERA • TAKTYKA • MECZE • TRANSFERY")

var careerDb: Node = null
var seasonDay := 1
