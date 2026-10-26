using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

/// <summary>Creates the first playable Football Empire interface at runtime.</summary>
public sealed class FootballEmpireBootstrap : MonoBehaviour
{
    private static readonly Color Navy = new Color(0.035f, 0.055f, 0.09f);
    private static readonly Color Panel = new Color(0.075f, 0.105f, 0.16f);
    private static readonly Color Green = new Color(0.35f, 0.88f, 0.48f);
    private static readonly Color Muted = new Color(0.68f, 0.73f, 0.81f);
    private Canvas canvas;
    private Font font;
    private RectTransform content;
    private int goalsFor;
    private int goalsAgainst;
    private int played;
    private string screen = "HOME";
    private string status = "Sezon 2026/27 • Pierwszy sezon";

    [RuntimeInitializeOnLoadMethod(RuntimeInitializeLoadType.AfterSceneLoad)]
    private static void Boot()
    {
        if (FindObjectOfType<FootballEmpireBootstrap>() != null) return;
        new GameObject("Football Empire Runtime").AddComponent<FootballEmpireBootstrap>();
    }

    private void Awake()
    {
        Application.targetFrameRate = 60;
        Screen.orientation = ScreenOrientation.Portrait;
        font = Resources.GetBuiltinResource<Font>("Arial.ttf");
        CreateUi();
        ShowHome();
    }

    private void CreateUi()
    {
        var canvasObject = new GameObject("Football Empire Canvas", typeof(Canvas), typeof(CanvasScaler), typeof(GraphicRaycaster));
        canvasObject.transform.SetParent(transform, false);
        canvas = canvasObject.GetComponent<Canvas>();
        canvas.renderMode = RenderMode.ScreenSpaceOverlay;
        var scaler = canvasObject.GetComponent<CanvasScaler>();
        scaler.uiScaleMode = CanvasScaler.ScaleMode.ScaleWithScreenSize;
        scaler.referenceResolution = new Vector2(1080, 1920);
        scaler.matchWidthOrHeight = 0.5f;
        if (FindObjectOfType<EventSystem>() == null)
        {
            var eventSystem = new GameObject("EventSystem", typeof(EventSystem), typeof(StandaloneInputModule));
            eventSystem.transform.SetParent(transform, false);
        }
        var background = MakePanel(canvas.transform, "Background", Navy);
        Stretch(background.GetComponent<RectTransform>());
        var root = new GameObject("Root", typeof(RectTransform), typeof(VerticalLayoutGroup));
        root.transform.SetParent(background.transform, false);
        var rt = root.GetComponent<RectTransform>();
        rt.anchorMin = new Vector2(0.04f, 0.025f); rt.anchorMax = new Vector2(0.96f, 0.975f);
        rt.offsetMin = Vector2.zero; rt.offsetMax = Vector2.zero;
        var layout = root.GetComponent<VerticalLayoutGroup>();
        layout.spacing = 18; layout.padding = new RectOffset(8, 8, 10, 10);
        layout.childControlWidth = true; layout.childControlHeight = false;
        layout.childForceExpandWidth = true; layout.childForceExpandHeight = false;
        AddText(root.transform, "FOOTBALL EMPIRE", 42, Green, FontStyle.Bold, 88);
        AddText(root.transform, "ULTIMATE CAREER  /  MANAGER MODE", 19, Muted, FontStyle.Bold, 42);
        content = new GameObject("Screen Content", typeof(RectTransform), typeof(VerticalLayoutGroup)).GetComponent<RectTransform>();
        content.SetParent(root.transform, false);
        var contentLayout = content.GetComponent<VerticalLayoutGroup>();
        contentLayout.spacing = 18; contentLayout.childControlWidth = true; contentLayout.childControlHeight = false;
        contentLayout.childForceExpandWidth = true; contentLayout.childForceExpandHeight = false;
        contentLayout.padding = new RectOffset(0, 0, 8, 8);
        var fitter = content.gameObject.AddComponent<ContentSizeFitter>();
        fitter.verticalFit = ContentSizeFitter.FitMode.PreferredSize;
        var nav = new GameObject("Navigation", typeof(RectTransform), typeof(HorizontalLayoutGroup), typeof(LayoutElement));
        nav.transform.SetParent(root.transform, false);
        nav.GetComponent<LayoutElement>().preferredHeight = 100;
        var navLayout = nav.GetComponent<HorizontalLayoutGroup>();
        navLayout.spacing = 8; navLayout.childControlWidth = true; navLayout.childControlHeight = true;
        navLayout.childForceExpandWidth = true; navLayout.childForceExpandHeight = true;
        AddButton(nav.transform, "KARIERA", () => { screen = "HOME"; ShowHome(); });
        AddButton(nav.transform, "SKŁAD", () => ShowSection("SKŁAD", "Pierwsza jedenastka", "GK  •  Bramkarz\nRB  •  Prawy obrońca\nCB  •  Środkowy obrońca\nCM  •  Środkowy pomocnik\nST  •  Napastnik"));
        AddButton(nav.transform, "TAKTYKA", () => ShowSection("TAKTYKA", "Ustawienie zespołu", "Formacja: 4-3-3\nMentalność: Zrównoważona\nPressing: Średni\nSzerokość: 55 / 100"));
        AddButton(nav.transform, "TRANSFER", () => ShowSection("TRANSFER", "Rynek transferowy", "Skauting i negocjacje będą kolejnym modułem.\nBudżet startowy: 5 000 000 €"));
    }

    private void ShowHome()
    {
        ClearContent();
        AddText(content, "CENTRUM KARIERY", 26, Color.white, FontStyle.Bold, 54);
        var club = MakePanel(content, "Club Card", Panel);
        AddText(club.transform, "YOUR CLUB  /  POLSKA", 17, Muted, FontStyle.Bold, 34);
        AddText(club.transform, "FC EMPIRE", 48, Color.white, FontStyle.Bold, 76);
        AddText(club.transform, "Sezon 2026/27     •     Liga krajowa", 20, Muted, FontStyle.Normal, 42);
        AddText(content, "NASTĘPNY MECZ", 23, Green, FontStyle.Bold, 44);
        var match = MakePanel(content, "Match Card", new Color(0.09f, 0.17f, 0.19f));
        AddText(match.transform, "FC EMPIRE     VS     CITY UNITED", 25, Color.white, FontStyle.Bold, 52);
        AddText(match.transform, "Kolejka " + (played + 1) + "  •  Stadion Empire", 18, Muted, FontStyle.Normal, 36);
        AddButton(content, "ROZEGRAJ MECZ", PlayMatch, Green, Navy, 76);
        AddText(content, "BILANS SEZONU", 23, Color.white, FontStyle.Bold, 44);
        var stats = MakePanel(content, "Season Stats", Panel);
        AddText(stats.transform, "MECZE  " + played + "       ZDOBYTE  " + goalsFor + "       STRACONE  " + goalsAgainst, 19, Color.white, FontStyle.Bold, 42);
        AddText(content, status, 17, Muted, FontStyle.Normal, 36);
    }

    private void PlayMatch()
    {
        int our = Random.Range(0, 4); int theirs = Random.Range(0, 4);
        played++; goalsFor += our; goalsAgainst += theirs;
        screen = "MATCH";
        ShowSection("WYNIK MECZU", "FC EMPIRE   " + our + " : " + theirs + "   CITY UNITED", "Mecz zakończony.\nStrzały: " + Random.Range(5, 17) + " : " + Random.Range(4, 15) + "\nPosiadanie piłki: " + Random.Range(42, 61) + "% : reszta\n\nTo pierwszy prototyp symulacji wyniku; pełny silnik boiskowy dopiero zbudujemy.");
    }

    private void ShowSection(string heading, string title, string details)
    {
        ClearContent();
        AddText(content, heading, 27, Green, FontStyle.Bold, 54);
        var panel = MakePanel(content, "Section Card", Panel);
        AddText(panel.transform, title, 32, Color.white, FontStyle.Bold, 62);
        AddText(panel.transform, details, 22, Muted, FontStyle.Normal, 260);
        AddButton(content, "WRÓĆ DO KARIERY", () => { screen = "HOME"; ShowHome(); }, Green, Navy, 76);
    }

    private void ClearContent()
    {
        for (int i = content.childCount - 1; i >= 0; i--) Destroy(content.GetChild(i).gameObject);
    }

    private GameObject MakePanel(Transform parent, string objectName, Color color)
    {
        var panel = new GameObject(objectName, typeof(RectTransform), typeof(Image), typeof(VerticalLayoutGroup), typeof(LayoutElement));
        panel.transform.SetParent(parent, false);
        panel.GetComponent<Image>().color = color;
        var layout = panel.GetComponent<VerticalLayoutGroup>();
        layout.padding = new RectOffset(24, 24, 18, 18); layout.spacing = 8;
        layout.childControlWidth = true; layout.childControlHeight = false;
        layout.childForceExpandWidth = true; layout.childForceExpandHeight = false;
        panel.GetComponent<LayoutElement>().preferredHeight = 160;
        return panel;
    }

    private Text AddText(Transform parent, string value, int size, Color color, FontStyle style, float height)
    {
        var go = new GameObject("Text - " + value, typeof(RectTransform), typeof(Text), typeof(LayoutElement));
        go.transform.SetParent(parent, false);
        var text = go.GetComponent<Text>(); text.font = font; text.text = value; text.fontSize = size;
        text.color = color; text.fontStyle = style; text.alignment = TextAnchor.MiddleLeft;
        text.horizontalOverflow = HorizontalWrapMode.Wrap; text.verticalOverflow = VerticalWrapMode.Overflow;
        text.resizeTextForBestFit = true; text.resizeTextMinSize = 14; text.resizeTextMaxSize = size;
        go.GetComponent<LayoutElement>().preferredHeight = height;
        return text;
    }

    private Button AddButton(Transform parent, string label, UnityEngine.Events.UnityAction action, Color? background = null, Color? foreground = null, float height = 68)
    {
        var go = new GameObject("Button - " + label, typeof(RectTransform), typeof(Image), typeof(Button), typeof(LayoutElement));
        go.transform.SetParent(parent, false);
        go.GetComponent<Image>().color = background ?? new Color(0.15f, 0.22f, 0.32f);
        go.GetComponent<LayoutElement>().preferredHeight = height;
        var button = go.GetComponent<Button>(); button.targetGraphic = go.GetComponent<Image>(); button.onClick.AddListener(action);
        var labelText = AddText(go.transform, label, 20, foreground ?? Color.white, FontStyle.Bold, height - 8);
        labelText.alignment = TextAnchor.MiddleCenter;
        var labelRect = labelText.GetComponent<RectTransform>(); labelRect.anchorMin = Vector2.zero; labelRect.anchorMax = Vector2.one; labelRect.offsetMin = Vector2.zero; labelRect.offsetMax = Vector2.zero;
        return button;
    }

    private static void Stretch(RectTransform rt)
    {
        rt.anchorMin = Vector2.zero; rt.anchorMax = Vector2.one; rt.offsetMin = Vector2.zero; rt.offsetMax = Vector2.zero;
    }
}
