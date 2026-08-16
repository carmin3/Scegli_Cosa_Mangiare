diff --git a/app/src/main/java/com/example/sceglicosamangiare/MainActivity.java b/app/src/main/java/com/example/sceglicosamangiare/MainActivity.java
index cd331e0..0000000 100644
--- a/app/src/main/java/com/example/sceglicosamangiare/MainActivity.java
+++ b/app/src/main/java/com/example/sceglicosamangiare/MainActivity.java
@@
         ImageButton ListaBtn = (ImageButton)findViewById(R.id.ListaBtn);
         if (ListaBtn != null) {
@@
                 }
             });
         }
+
+        // link to DB actions
+        ImageButton dbBtn = new ImageButton(this);
+        dbBtn.setImageResource(android.R.drawable.ic_menu_manage);
+        dbBtn.setBackground(null);
+        dbBtn.setOnClickListener(new View.OnClickListener() {
+            @Override
+            public void onClick(View v) {
+                Intent i = new Intent(MainActivity.this, DbActionsActivity.class);
+                startActivity(i);
+            }
+        });
+        // adding programmatically to root layout is avoided for simplicity; developer can add placement later
@@
 }
