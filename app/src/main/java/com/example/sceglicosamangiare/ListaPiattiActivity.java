*** Begin Patch
*** Update File: app/src/main/java/com/example/sceglicosamangiare/ListaPiattiActivity.java
@@
-    private DataBaseHelper dataBaseHelper;
+    private PiattoRepository dataBaseHelper;
@@
-        dataBaseHelper = new DataBaseHelper(ListaPiattiActivity.this);
+        dataBaseHelper = new PiattoRepository(ListaPiattiActivity.this);
*** End Patch
