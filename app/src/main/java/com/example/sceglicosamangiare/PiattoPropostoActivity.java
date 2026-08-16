*** Begin Patch
*** Update File: app/src/main/java/com/example/sceglicosamangiare/PiattoPropostoActivity.java
@@
-    private DataBaseHelper dataBaseHelper;
+    private PiattoRepository dataBaseHelper;
@@
-        dataBaseHelper = new DataBaseHelper(PiattoPropostoActivity.this);
-        listaPiatti = dataBaseHelper.getAllData();
+        dataBaseHelper = new PiattoRepository(PiattoPropostoActivity.this);
+        listaPiatti = dataBaseHelper.getAllData();
*** End Patch
