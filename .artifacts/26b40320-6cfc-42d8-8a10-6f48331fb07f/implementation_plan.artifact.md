# Implementazione Navigazione e Viste Calendario

Questo piano descrive l'implementazione della navigazione tramite swipe nella vista quotidiana e l'aggiunta delle viste settimanale e mensile nell'attività `CalendarioActivity`.

## User Review Required

> [!IMPORTANT]
> L'implementazione prevede il passaggio tra tre stati della UI nello stesso layout. Utilizzeremo un'icona in alto a destra per alternare tra:
> - **Vista Quotidiana**: (Default) Navigazione tramite swipe (DX->SX: Successivo, SX->DX: Precedente).
> - **Vista Settimanale**: Panoramica di 5 giorni con schede a forma di pillola e altezze variabili.
> - **Vista Mensile**: Griglia dei giorni del mese con indicatori colorati per i pasti inseriti.

> [!NOTE]
> Le icone utilizzate saranno:
> - Quotidiana -> Settimanale: `ic_view_day`
> - Settimanale -> Mensile: `ic_view_week` (4 quadratini)
> - Mensile -> Quotidiana: `ic_calendar_month` (simile a `calendar-today`)

## Proposed Changes

### [Component] Layout XML

#### [MODIFY] [activity_calendario.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/activity_calendario.xml)
- Aggiunta dell'ImageButton `viewSwitchBtn` nel `RelativeLayout` superiore.
- Inserimento di un `FrameLayout` come contenitore principale per le tre viste.
- Definizione della `dailyView` (ScrollView esistente), `weeklyView` (nuovo ScrollView/LinearLayout) e `monthlyView` (nuovo LinearLayout con GridView).

#### [NEW] [item_weekly_day.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/item_weekly_day.xml)
- Layout per le schede della vista settimanale (stile pillola, ombra, intestazione data e riepilogo pasti).

#### [NEW] [item_calendar_day.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/item_calendar_day.xml)
- Layout per le celle della griglia mensile (numero del giorno in un rettangolo).

---

### [Component] Database & Business Logic

#### [MODIFY] [DataBaseHelper.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/DataBaseHelper.java)
- Aggiunta del metodo `getPianiPastoForMonth(String yearMonth)` per caricare i dati della vista mensile in un'unica query.
- Aggiunta del metodo `isDayPopulated(String date)` o simile (facoltativo, può essere gestito nella query mensile).

---

### [Component] Activity Logic

#### [MODIFY] [CalendarioActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/CalendarioActivity.java)
- Implementazione di `GestureDetector.SimpleOnGestureListener` per rilevare gli swipe.
- Gestione dello stato della vista corrente (`ViewMode`).
- Logica di switch tra le viste e aggiornamento dell'icona.
- Metodi per popolare la vista settimanale calcolando Ieri, Oggi e i 3 giorni successivi.
- Implementazione di un Adapter personalizzato per la `GridView` mensile.
- Integrazione della navigazione: cliccare su un giorno (settimanale o mensile) riporta alla vista quotidiana per quel giorno.

## Verification Plan

### Automated Tests
- N/A (Progetto Java legacy senza suite di test automatizzati configurata).

### Manual Verification
1. **Navigazione Swipe**: Verificare che lo swipe a sinistra porti al giorno dopo e lo swipe a destra al giorno prima nella vista quotidiana.
2. **Switch Viste**: Cliccare sull'icona in alto a destra e verificare la transizione corretta delle icone e dei layout (Quotidiana -> Settimanale -> Mensile -> Quotidiana).
3. **Vista Settimanale**:
   - Verificare la presenza di 5 giorni.
   - Verificare le altezze variabili (Oggi più grande, Ieri più piccolo).
   - Verificare che il riepilogo piatti sia corretto.
   - Cliccare su un giorno e verificare che si apra la vista quotidiana corretta.
4. **Vista Mensile**:
   - Verificare che la griglia mostri i giorni del mese corrente.
   - Verificare che i giorni con pasti salvati siano colorati in verde chiaro.
   - Cliccare su un giorno e verificare l'apertura della vista quotidiana.
