package net.eltiburon.minimax.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [OportunidadEntity::class, ParticipacionEntity::class, UsuarioEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MiniMaxDatabase : RoomDatabase() {
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun participacionDao(): ParticipacionDao
    abstract fun usuarioDao(): UsuarioDao
}

/**
 * v1 -> v2: agrega la tabla de usuarios. Se hace por migración (en vez de
 * fallbackToDestructiveMigration) para no perder las oportunidades y participaciones
 * ya persistidas en instalaciones existentes.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS usuarios (
                id TEXT NOT NULL PRIMARY KEY,
                nombre TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                rol TEXT
            )
            """.trimIndent()
        )
    }
}

/**
 * v2 -> v3: asigna a las oportunidades demo una imagen mock acorde a su producto (ver
 * [MockImagenes]). Solo toca filas sin imagen propia (imagenUri IS NULL), para no pisar las
 * fotos que un proveedor haya subido con la cámara.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        MockImagenes.porId.forEach { (id, url) ->
            db.execSQL(
                "UPDATE oportunidades SET imagenUri = ? WHERE id = ? AND imagenUri IS NULL",
                arrayOf<Any>(url, id)
            )
        }
    }
}

/**
 * v3 -> v4: agrega la columna fotoUri a usuarios, para guardar la foto de perfil tomada con la
 * cámara. Se hace por migración para no perder las cuentas ya registradas.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE usuarios ADD COLUMN fotoUri TEXT")
    }
}
