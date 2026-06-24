package net.eltiburon.minimax.data.local

/**
 * URLs de imágenes "de mentira" para el catálogo demo. Usamos loremflickr, que devuelve una
 * foto real de Flickr a partir de palabras clave, así cada publicación muestra algo acorde a
 * su producto (aceite, café, teclado, …) sin tener que bundlear un drawable por cada uno.
 * El parámetro `lock` fija la foto para que no cambie en cada carga.
 *
 * Se consume desde el seed de [net.eltiburon.minimax.data.OportunidadRepository] (instalaciones
 * nuevas) y desde [MIGRATION_2_3] (instalaciones existentes), para que ambas vean lo mismo.
 */
object MockImagenes {

    private fun url(keywords: String, lock: Int) =
        "https://loremflickr.com/400/300/$keywords?lock=$lock"

    /** id de oportunidad -> URL de imagen acorde al producto. */
    val porId: Map<String, String> = mapOf(
        "1" to url("sunflower,oil", 1),
        "2" to url("rice", 2),
        "3" to url("toilet,paper", 3),
        "4" to url("detergent,cleaning", 4),
        "5" to url("mate,tea", 5),
        "6" to url("pasta", 6),
        "7" to url("sugar", 7),
        "8" to url("salt", 8),
        "9" to url("canned,tomato", 9),
        "101" to url("headphones", 101),
        "102" to url("coffee,beans", 102),
        "103" to url("smartwatch", 103),
        "104" to url("pillow", 104),
        "105" to url("moka,coffee", 105),
        "106" to url("framed,art", 106),
        "107" to url("desk,lamp", 107),
        "108" to url("mate,tea", 108),
        "109" to url("keyboard", 109),
        "110" to url("cookware,pots", 110)
    )
}
