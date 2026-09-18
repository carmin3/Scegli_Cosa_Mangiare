# Implementazione Impostazioni Dieta Bilanciata

Aggiunta di una nuova sezione nelle impostazioni per permettere all'utente di personalizzare i "pesi" dei nutrienti utilizzati nell'algoritmo di scelta casuale bilanciata.

## User Review Required

> [!IMPORTANT]
> I pesi predefiniti verranno impostati seguendo la logica attuale di `CalendarioActivity`:
> - Carne Rossa: 5
> - Carne Bianca: 20
> - Pesce: 45
> - Vegetariano: 20
> L'utente potrà variare questi valori tramite slider (range 0-100).

## Proposed Changes

### [Gestione Dati]

#### [NEW] [WeightManager.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/WeightManager.java)
Creazione di una classe utility per gestire il salvataggio e il recupero dei pesi tramite `SharedPreferences`.

### [Interfaccia Utente]

#### [MODIFY] [activity_impostazioni.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/activity_impostazioni.xml)
Aggiunta della voce "Impostazioni dieta Bilanciata" all'elenco.

#### [MODIFY] [ImpostazioniActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/ImpostazioniActivity.java)
Gestione del click sulla nuova voce per aprire la nuova Activity.

#### [NEW] [activity_dieta_bilanciata_settings.xml](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/res/layout/activity_dieta_bilanciata_settings.xml)
Nuovo layout che segue lo stile dell'app, contenente:
- Header con pulsante Home e Titolo.
- Slider per ogni nutriente (Carne Rossa, Carne Bianca, Pesce, Vegetariano).
- Pulsanti "Applica" (nascosto di default) e "Ripristina".

#### [NEW] [DietaBilanciataSettingsActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/DietaBilanciataSettingsActivity.java)
Nuova Activity per gestire l'interazione con gli slider e i pulsanti.

### [Logica Applicativa]

#### [MODIFY] [CalendarioActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/CalendarioActivity.java)
Aggiornamento del metodo `getWeightedRandomProteina` per utilizzare i pesi salvati dall'utente.

#### [MODIFY] [PiattoPropostoActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/PiattoPropostoActivity.java)
Aggiornamento del metodo `sceltaCasualeProteina` per utilizzare i pesi salvati dall'utente.

## Verification Plan

### Manual Verification
1. Aprire le Impostazioni e verificare la presenza della nuova voce.
2. Cliccare sulla voce e verificare che la nuova pagina si apra con gli slider impostati sui valori di default.
3. Muovere uno slider e verificare che il tasto "Applica" appaia.
4. Cliccare "Applica" e verificare che i valori vengano salvati (riaprendo la pagina).
5. Cliccare "Ripristina" e verificare che gli slider tornino ai valori di fabbrica.
6. Verificare che la generazione casuale del calendario e del piatto proposto rispetti i nuovi pesi impostati (ad esempio, impostando un peso a 0 per un nutriente, questo non dovrebbe più comparire).
