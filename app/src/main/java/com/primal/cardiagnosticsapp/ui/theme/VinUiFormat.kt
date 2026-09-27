package com.primal.cardiagnosticsapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.primal.cardiagnosticsapp.R

@Composable
fun localizedVinPlace(key: String?): String? {
    return when (key) {
        "usa", "USA" -> stringResource(R.string.country_usa)
        "canada", "Canada" -> stringResource(R.string.country_canada)
        "mexico", "Mexico" -> stringResource(R.string.country_mexico)
        "japan", "Japan" -> stringResource(R.string.country_japan)
        "korea", "Korea" -> stringResource(R.string.country_korea)
        "germany", "Germany" -> stringResource(R.string.country_germany)
        "france", "France" -> stringResource(R.string.country_france)
        "spain", "Spain" -> stringResource(R.string.country_spain)
        "sweden", "Sweden" -> stringResource(R.string.country_sweden)
        "italy", "Italy" -> stringResource(R.string.country_italy)
        "china", "China" -> stringResource(R.string.country_china)
        "czech_republic", "Czech Republic" -> stringResource(R.string.country_czech_republic)
        "uk", "UK" -> stringResource(R.string.country_uk)

        "europe_mixed_v",
        "France/Spain/Austria/Croatia" ->
            stringResource(R.string.region_europe_mixed_v)

        "europe_mixed_t",
        "Switzerland/Czech Republic/Hungary/Portugal" ->
            stringResource(R.string.region_europe_mixed_t)

        "europe_mixed_y",
        "Sweden/Finland/Belgium" ->
            stringResource(R.string.region_europe_mixed_y)

        else -> key
    }
}