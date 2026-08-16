*** Begin Patch
*** Update File: app/src/main/java/com/example/sceglicosamangiare/PiattoDetailActivity.java
@@
-    private DataBaseHelper dataBaseHelper;
+    private PiattoRepository dataBaseHelper;
@@
-        dataBaseHelper = new DataBaseHelper(PiattoDetailActivity.this);
+        dataBaseHelper = new PiattoRepository(PiattoDetailActivity.this);
*** End Patch
