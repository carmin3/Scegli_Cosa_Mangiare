*** Begin Patch
*** Update File: app/src/main/java/com/example/sceglicosamangiare/AggiuntaPiattiActivity.java
@@
-    private DataBaseHelper dataBaseHelper;
+    private PiattoRepository dataBaseHelper;
@@
-        dataBaseHelper = new DataBaseHelper(AggiuntaPiattiActivity.this);
+        dataBaseHelper = new PiattoRepository(AggiuntaPiattiActivity.this);
*** End Patch
