using UnityEngine;
using UnityEngine.SceneManagement;

public sealed class FootballEmpireMain : MonoBehaviour
{
    private enum ScreenMode { Home, Career, Match }
    private ScreenMode screen = ScreenMode.Home;
    private string clubName = "Warszawa FC";
    private int season = 2026;
    private int week = 1;
    private int wins = 0, draws = 0, losses = 0;
    private int homeGoals, awayGoals;
    private float matchClock;
    private bool matchRunning;
    private GUIStyle titleStyle, headingStyle, bodyStyle, buttonStyle, smallStyle;
    private Texture2D backgroundTexture, panelTexture, accentTexture, pitchTexture;

    private void Awake()
    {
        Application.targetFrameRate = 60;
        Screen.orientation = ScreenOrientation.Portrait;
        backgroundTexture = MakeTexture(new Color32(13, 22, 39, 255));
        panelTexture = MakeTexture(new Color32(24, 37, 58, 255));
        accentTexture = MakeTexture(new Color32(40, 205, 139, 255));
        pitchTexture = MakeTexture(new Color32(26, 105, 73, 255));
    }

    private Texture2D MakeTexture(Color color)
    {
        var texture = new Texture2D(1, 1);
        texture.SetPixel(0, 0, color);
        texture.Apply();
        return texture;
    }

    private void OnDestroy()
    {
        if (backgroundTexture != null) Destroy(backgroundTexture);
        if (panelTexture != null) Destroy(panelTexture);
        if (accentTexture != null) Destroy(accentTexture);
        if (pitchTexture != null) Destroy(pitchTexture);
    }

    private void PrepareStyles()
    {
        float scale = Mathf.Clamp(Screen.width / 420f, 0.8f, 1.6f);
        titleStyle = new GUIStyle(GUI.skin.label) { fontSize = Mathf.RoundToInt(30 * scale), fontStyle = FontStyle.Bold, normal = { textColor = Color.white }, wordWrap = true };
        headingStyle = new GUIStyle(GUI.skin.label) { fontSize = Mathf.RoundToInt(20 * scale), fontStyle = FontStyle.Bold, normal = { textColor = Color.white }, wordWrap = true };
        bodyStyle = new GUIStyle(GUI.skin.label) { fontSize = Mathf.RoundToInt(15 * scale), normal = { textColor = new Color(0.82f, 0.87f, 0.93f) }, wordWrap = true };
        smallStyle = new GUIStyle(bodyStyle) { fontSize = Mathf.RoundToInt(12 * scale), normal = { textColor = new Color(0.63f, 0.71f, 0.81f) } };
        buttonStyle = new GUIStyle(GUI.skin.button) { fontSize = Mathf.RoundToInt(16 * scale), fontStyle = FontStyle.Bold, alignment = TextAnchor.MiddleCenter, wordWrap = true, padding = new RectOffset(10, 10, 12, 12), normal = { textColor = Color.white, background = accentTexture }, hover = { textColor = Color.white, background = accentTexture }, active = { textColor = Color.white, background = accentTexture } };
    }

    private void OnGUI()
    {
        PrepareStyles();
        GUI.depth = 0;
        GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), backgroundTexture);
        float margin = Screen.width * 0.055f;
        float width = Screen.width - margin * 2;
        float y = Screen.height * 0.045f;
        GUI.Label(new Rect(margin, y, width, 24), "FOOTBALL EMPIRE  /  MANAGER", smallStyle);
        y += 32;
        if (screen == ScreenMode.Home) DrawHome(margin, width, y);
        else if (screen == ScreenMode.Career) DrawCareer(margin, width, y);
        else DrawMatch(margin, width, y);
        GUI.Label(new Rect(margin, Screen.height - 28, width, 20), "WERSJA ROBOCZA • UNITY 6", smallStyle);
    }

    private void DrawHome(float x, float width, float y)
    {
        GUI.Label(new Rect(x, y, width, 100), "Zbuduj swoją\npiłkarską dynastię.", titleStyle);
        y += 112;
        DrawPanel(new Rect(x, y, width, 100));
        GUI.Label(new Rect(x + 16, y + 14, width - 32, 28), "TWOJA KARIERA ZACZYNA SIĘ TUTAJ", smallStyle);
        GUI.Label(new Rect(x + 16, y + 40, width - 32, 45), "Zarządzaj klubem. Ustalaj taktykę. Wygrywaj.", bodyStyle);
        y += 120;
        if (GUI.Button(new Rect(x, y, width, 60), "ROZPOCZNIJ KARIERĘ", buttonStyle)) screen = ScreenMode.Career;
        y += 76;
        DrawPanel(new Rect(x, y, width, 150));
        GUI.Label(new Rect(x + 16, y + 14, width - 32, 28), "CO BUDUJEMY", headingStyle);
        GUI.Label(new Rect(x + 16, y + 52, width - 32, 90), "• Tryb kariery menedżera\n• Taktyka, skład i transfery\n• Mecze 2D z poruszającymi się zawodnikami", bodyStyle);
    }

    private void DrawCareer(float x, float width, float y)
    {
        GUI.Label(new Rect(x, y, width, 44), "CENTRUM MENEDŻERA", titleStyle);
        y += 54;
        DrawPanel(new Rect(x, y, width, 120));
        GUI.Label(new Rect(x + 16, y + 12, width - 32, 30), clubName, headingStyle);
        GUI.Label(new Rect(x + 16, y + 47, width - 32, 25), "Sezon " + season + "/" + (season + 1) + "  •  Kolejka " + week, bodyStyle);
        GUI.Label(new Rect(x + 16, y + 78, width - 32, 25), "Forma: " + wins + " Z  " + draws + " R  " + losses + " P", smallStyle);
        y += 138;
        DrawPanel(new Rect(x, y, width, 124));
        GUI.Label(new Rect(x + 16, y + 12, width - 32, 26), "TAKTYKA MECZOWA", headingStyle);
        GUI.Label(new Rect(x + 16, y + 45, width - 32, 58), "Ustawienie 4-3-3\nStyl: zbalansowany • Pressing: średni", bodyStyle);
        y += 142;
        if (GUI.Button(new Rect(x, y, width, 56), "ROZEGRAJ MECZ 2D", buttonStyle))
        {
            homeGoals = 0; awayGoals = 0; matchClock = 0; matchRunning = true; screen = ScreenMode.Match;
        }
        y += 68;
        if (GUI.Button(new Rect(x, y, width, 50), "ZMIENIAJ KLUB", buttonStyle))
            clubName = clubName == "Warszawa FC" ? "Kraków United" : clubName == "Kraków United" ? "Gdańsk Athletic" : "Warszawa FC";
        y += 62;
        if (GUI.Button(new Rect(x, y, width, 46), "WRÓĆ DO MENU", buttonStyle)) screen = ScreenMode.Home;
    }

    private void DrawMatch(float x, float width, float y)
    {
        if (matchRunning) matchClock += Time.deltaTime * 5f;
        GUI.Label(new Rect(x, y, width, 38), clubName + "  vs  Rywale FC", headingStyle);
        y += 46;
        DrawPanel(new Rect(x, y, width, 64));
        GUI.Label(new Rect(x + 12, y + 10, width - 24, 44), string.Format("{0:00}'       {1}  -  {2}", Mathf.Min(90, (int)matchClock), homeGoals, awayGoals), titleStyle);
        y += 78;
        float pitchHeight = Mathf.Min(Screen.height * 0.42f, width * 1.18f);
        Rect pitch = new Rect(x, y, width, pitchHeight);
        GUI.DrawTexture(pitch, pitchTexture);
        DrawPitchLines(pitch);
        DrawPlayers(pitch);
        y += pitchHeight + 14;
        if (matchClock < 90 && GUI.Button(new Rect(x, y, width, 54), "SYMULUJ AKCJĘ", buttonStyle))
        {
            matchClock = Mathf.Min(90, matchClock + Random.Range(8, 18));
            int eventRoll = Random.Range(0, 100);
            if (eventRoll < 24) homeGoals++;
            else if (eventRoll < 42) awayGoals++;
        }
        y += 64;
        if (matchClock >= 90 && GUI.Button(new Rect(x, y, width, 54), "ZAKOŃCZ MECZ", buttonStyle))
        {
            if (homeGoals > awayGoals) wins++; else if (homeGoals == awayGoals) draws++; else losses++;
            week++; matchRunning = false; screen = ScreenMode.Career;
        }
        if (matchClock < 90) GUI.Label(new Rect(x, y + 60, width, 36), "Dotknij „Symuluj akcję”, aby rozegrać kolejną sytuację.", smallStyle);
    }

    private void DrawPitchLines(Rect p)
    {
        float line = Mathf.Max(2, Screen.width * 0.004f);
        DrawRect(new Rect(p.x + line, p.y + line, p.width - 2 * line, line), Color.white);
        DrawRect(new Rect(p.x + line, p.y + p.height - 2 * line, p.width - 2 * line, line), Color.white);
        DrawRect(new Rect(p.x + line, p.y + line, line, p.height - 2 * line), Color.white);
        DrawRect(new Rect(p.x + p.width - 2 * line, p.y + line, line, p.height - 2 * line), Color.white);
        DrawRect(new Rect(p.center.x - line / 2, p.y, line, p.height), new Color(1, 1, 1, 0.7f));
        DrawRect(new Rect(p.center.x - p.width * .18f, p.y + p.height * .32f, p.width * .36f, line), Color.white);
        DrawRect(new Rect(p.center.x - p.width * .18f, p.y + p.height * .68f, p.width * .36f, line), Color.white);
        DrawRect(new Rect(p.center.x - p.width * .18f, p.y + p.height * .32f, line, p.height * .36f), Color.white);
        DrawRect(new Rect(p.center.x + p.width * .18f - line, p.y + p.height * .32f, line, p.height * .36f), Color.white);
    }

    private void DrawPlayers(Rect p)
    {
        float[,] home = { { .5f,.92f },{ .22f,.78f },{ .42f,.80f },{ .62f,.80f },{ .80f,.78f },{ .25f,.62f },{ .5f,.65f },{ .75f,.62f },{ .22f,.40f },{ .5f,.38f },{ .78f,.40f } };
        float[,] away = { { .5f,.08f },{ .22f,.22f },{ .42f,.20f },{ .62f,.20f },{ .80f,.22f },{ .25f,.38f },{ .5f,.35f },{ .75f,.38f },{ .22f,.60f },{ .5f,.62f },{ .78f,.60f } };
        float r = Mathf.Clamp(Screen.width * .022f, 7, 14);
        for (int i = 0; i < 11; i++)
        {
            DrawRect(new Rect(p.x + home[i,0] * p.width - r, p.y + home[i,1] * p.height - r, r * 2, r * 2), new Color32(44, 221, 155, 255));
            DrawRect(new Rect(p.x + away[i,0] * p.width - r, p.y + away[i,1] * p.height - r, r * 2, r * 2), new Color32(245, 92, 100, 255));
        }
        float bx = p.x + p.width * (.5f + Mathf.Sin(matchClock * .13f) * .18f);
        float by = p.y + p.height * (.5f + Mathf.Cos(matchClock * .17f) * .17f);
        DrawRect(new Rect(bx - r * .35f, by - r * .35f, r * .7f, r * .7f), Color.white);
    }

    private void DrawPanel(Rect rect)
    {
        GUI.DrawTexture(rect, panelTexture);
        DrawRect(new Rect(rect.x, rect.y, 4, rect.height), accentTexture == null ? Color.green : new Color32(40, 205, 139, 255));
    }

    private void DrawRect(Rect rect, Color color)
    {
        Color old = GUI.color;
        GUI.color = color;
        GUI.DrawTexture(rect, Texture2D.whiteTexture);
        GUI.color = old;
    }
}
