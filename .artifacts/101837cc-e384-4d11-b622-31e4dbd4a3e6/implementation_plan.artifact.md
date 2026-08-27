# Backup e Ripristino Calendario

Implementazione della gestione del database del calendario (Backup, Ripristino, Pulizia), seguendo il modello già esistente per il database delle ricette.

## Proposed Changes

### Risorse e UI

#### [MODIFY] [strings.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/values/strings.xml)
Aggiunta delle stringhe necessarie per il nuovo menu e l'interfaccia di gestione calendario.

#### [MODIFY] [activity_impostazioni.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/activity_impostazioni.xml)
Aggiunta della voce "Backup e Ripristino calendario" nella lista delle impostazioni.

#### [NEW] [activity_calendar_db_actions.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/activity_calendar_db_actions.xml)
Nuovo layout per le azioni sul database del calendario, basato su `activity_db_actions.xml`.

---

### Logica di Business e Database

#### [MODIFY] [DataBaseHelper.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/DataBaseHelper.java)
Implementazione dei metodi per esportare, importare e pulire la tabella `PIANO_PASTO_TABLE`:
- `exportCalendarToOutputStream(OutputStream os)`: Esporta i dati in formato JSON.
- `importCalendarFromInputStream(InputStream is)`: Importa i dati con logica di merge (sovrascrive se la data esiste già).
- `clearCalendar()`: Elimina tutti i record dalla tabella del calendario.

---

### Attività e Navigazione

#### [MODIFY] [ImpostazioniActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/ImpostazioniActivity.java)
Configurazione del click listener per navigare verso la nuova attività `CalendarDbActionsActivity`.

#### [NEW] [CalendarDbActionsActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/CalendarDbActionsActivity.java)
Nuova attività che gestisce l'interfaccia utente e le chiamate al database per il backup/ripristino del calendario.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/AndroidManifest.xml)
Registrazione della nuova attività `CalendarDbActionsActivity`.

## Verification Plan

### Manual Verification
- Navigare in Impostazioni e verificare la presenza della nuova voce.
- Accedere alla pagina di backup calendario.
- Testare l'esportazione: verificare che venga generato un file JSON con i dati del calendario.
- Testare la pulizia: verificare che il calendario venga svuotato.
- Testare l'importazione: verificare che i dati vengano ripristinati correttamente dal file JSON precedentemente esportato, gestendo le sovrapposizioni.
