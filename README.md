Adatmodellek:
1. User
2. Appointment

Activityk:
1. BaseActivity
2. ManageActivity
3. ProfileActivity
4. RegisterActivity
5. SignInActivity
6. SplashActivity

Animációk:
1. Törölt és lemondott időpontok eltűnése
2. Splash screenen a logo villogása

Lifecycle Hook-ok:
1. BookingFragmentben: onStart,onResume
2. AdvertiseFragmentben: onStart, onResume

Android erőforrások:
1. Naptárhoz való hozzáférés
2. Telefon alkalmazáshoz való hozzáférés (hívás indítása)

CRUD műveletek:
1. Create: időpont létrehozás
2. Read: időpontok listázása
3. Update: időpont módosítása
4. Delete: időpont törlése

Komplex Firestore lekérdezések:
- Több is található az AppointmentRepository-ban pl:
1. return appointmentsRef.whereEqualTo("advertiserID", uid).orderBy("date", Query.Direction.ASCENDING).get();
2. return appointmentsRef.whereEqualTo("date",selectedDate).whereNotEqualTo("advertiserID",uid).whereEqualTo("bookedByID","null").get();
3. return appointmentsRef.whereEqualTo("bookedByID", uid).orderBy("date", Query.Direction.ASCENDING).orderBy("time", Query.Direction.ASCENDING).get();
