# Nuova Logica di Generazione Menù

Implementazione del nuovo algoritmo di generazione pasti basato su 4 tag: Portata, Nutrienti, Dominanza Nutrizionale e Profilo Gustativo.

## Proposed Changes

### [Model & Database]

#### [MODIFY] [Piatto.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/Piatto.java)
- Aggiunta dei campi `dominanzaNutrizionale` (String) e `profiloGustativo` (String).
- Aggiornamento dei costruttori, toString e getter/setter.

#### [MODIFY] [DataBaseHelper.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/DataBaseHelper.java)
- Aggiunta delle costanti `COLUMN_DOMINANZA_NUTRIZIONALE` e `COLUMN_PROFILO_GUSTATIVO`.
- Aggiornamento di `onCreate` per includere le nuove colonne.
- Aggiornamento di `onUpgrade` (versione 6) per aggiungere le colonne al database esistente.
- Aggiornamento dei metodi CRUD (`insertPersonal`, `updatePersonalById`, `getPiattoFromCursor`) e di export/import JSON.

#### [MODIFY] [BaseDbHelper.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/BaseDbHelper.java)
- Aggiornamento della lettura dei piatti dal database base per includere i nuovi campi.

---

### [Logic & Repository]

#### [NEW] [Pasto.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/Pasto.java)
- Classe helper per rappresentare un pasto completo (Primo, Secondo, Contorno, Piatto Unico) e il nutriente associato.

#### [MODIFY] [PiattoRepository.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/PiattoRepository.java)
- Implementazione del metodo `generaPasto(String nutrienteScelto)` che segue le fasi descritte:
    - **Fase A**: Scelta casuale del primo piatto (Primo, Secondo o Piatto Unico) per il nutriente dato.
    - **Fase B**: Valutazione della portata e dominanza.
    - **Fase C**: Incastro con la portata mancante applicando i filtri nutrizionali (Carbo-Puro ↔ Proteina-Pura) e gustativi (Terra-Forte vs Mare).
    - **Fase D**: Aggiunta del contorno (Fibre/Grassi) compatibile.

---

### [Activities]

#### [MODIFY] [PiattoPropostoActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/PiattoPropostoActivity.java)
- Aggiornamento per utilizzare `PiattoRepository.generaPasto`.
- Gestione della visibilità dei piatti nella UI (es. se è Piatto Unico, pulire Primo e Secondo).

#### [MODIFY] [CalendarioActivity.java](file:///C:/Users/Carmin3/AndroidStudioProjects/ScegliCosaMangiare/app/src/main/java/com/example/sceglicosamangiare/CalendarioActivity.java)
- Aggiornamento del metodo `generateMenu` per utilizzare la nuova logica di incastro invece di pescare piatti indipendenti.

## Verification Plan

### Automated Tests
- Non sono presenti test automatizzati nel progetto, la verifica sarà manuale.

### Manual Verification
- Avvio dell'app e generazione di nuovi menù nella sezione "Lo Chef Consiglia...".
- Verifica che i piatti proposti seguano le regole di incastro (es. no Mare + Terra-Forte).
- Verifica che la scelta del nutriente rimanga pesata secondo le impostazioni.
- Verifica del funzionamento nel calendario.
