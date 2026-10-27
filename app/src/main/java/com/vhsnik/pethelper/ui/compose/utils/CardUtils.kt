package com.vhsnik.pethelper.ui.compose.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.health.PetCardChip
import com.vhsnik.pethelper.ui.theme.*
import com.vhsnik.pethelper.ui.viewModel.PetFields
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.forEachIndexed

@Composable
fun DropdownField(
    modifier: Modifier,
    value: String,
    content: List<String>,
    onClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier.wrapContentSize(Alignment.TopStart)) {
        CardWithDropdown(Modifier.fillMaxWidth(), value) { isExpanded = true }
        DropdownMenuPattern(
            isExpanded, { isExpanded = false },
            content
        ) {
            onClick(it)
            isExpanded = false
        }
    }
}

@Composable
fun CardWithDropdown(modifier: Modifier = Modifier, value: String, onClick: () -> Unit) {
    Card(
        modifier
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, lightBrown)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(
                value,
                12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                painterResource(R.drawable.back), "choose value",
                Modifier
                    .clickable(interactionSource = null, indication = null) { onClick() }
                    .size(16.dp)
                    .rotate(-90f)
            )

        }
    }
}

@Composable
fun <T> PagerCard(
    label: String,
    onAddItem: (() -> Unit)? = null,
    content: List<T>,
    isNote: Boolean = false,
    itemContent: @Composable (item: T, hasLine: Boolean) -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                if (onAddItem != null) {
                    Icon(
                        painterResource(R.drawable.prime_plus),
                        contentDescription = "Add item",
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null
                        ) { onAddItem() }
                    )
                }
            }
            HorizontalDivider(
                Modifier.fillMaxWidth(),
                1.dp,
                dividerColor
            )
            if (content.isNotEmpty()) {
                val pages = remember(content) { content.chunked(4) }
                val pagerState = rememberPagerState(pageCount = { pages.size })
                HorizontalPager(
                    pagerState,
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) { currentPage ->
                    val pageItems = pages[currentPage]
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (isNote) 142.dp else 215.dp)
                    ) {
                        pageItems.forEachIndexed { index, item ->
                            val hasLine = !(pageItems.size == 4 && index == 3)
                            itemContent(item, hasLine)

                        }
                    }
                }
            }
        }

    }
}

@Composable
fun PagerCardItem(
    icon: Int,
    label: String,
    note: String,
    date: String,
    time: String,
    hasAddInfoLeft: Boolean,
    hasAddInfoRight: Boolean,
    hasLine: Boolean = true,
    hasNote: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    hasMarkAsDone: Boolean = false,
    onMarkAsDoneClick: () -> Unit = {},
    hasMenu: Boolean = true
) {
    var isMenuOpened by remember { mutableStateOf(false) }
    val parsedDate =
        remember(date) {
            runCatching {
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(dateParser)
            }.getOrDefault(date)
        }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(icon), "Item Icon")
                Spacer(Modifier.width(10.dp))
                Column(Modifier.wrapContentSize()) {
                    TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                    if (hasAddInfoLeft && hasNote) {
                        Spacer(Modifier.height(4.dp))
                        TextMaker(note, 10.sp, Color(0xFF727272), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(
                Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.wrapContentSize()) {
                    TextMaker(parsedDate, 10.sp, fontWeight = FontWeight.Medium)
                    if (hasAddInfoRight) {
                        Spacer(Modifier.height(4.dp))
                        TextMaker(time, 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.width(10.dp))
                if (hasMenu) {
                    Box {
                        Icon(
                            Icons.Default.MoreVert,
                            "More Options",
                            Modifier
                                .size(18.dp)
                                .clickable {
                                    isMenuOpened = true
                                },
                        )
                        DropdownMoreOptions(
                            isMenuOpened,
                            { isMenuOpened = false },
                            onEdit,
                            onDelete,
                            hasMarkAsDone,
                            onMarkAsDoneClick
                        )
                    }
                }
            }
        }
        if (hasLine) {
            HorizontalDivider(thickness = 1.dp, color = dividerColor)
        }
    }
}

@Composable
fun PetInfoCard(
    pet: PetFields,
    onCardClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(indication = null, interactionSource = null) { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val photoModifier = remember {
                Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            }
            if (pet.photo == null) {
                Image(
                    painterResource(R.drawable.no_photo_profile),
                    modifier = photoModifier,
                    contentDescription = "Pet photo",
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = pet.photo,
                    modifier = photoModifier,
                    contentDescription = "Pet photo",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(30.dp))
            Column(
                Modifier.height(120.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(if (pet.animal == "Dog") R.drawable.dog else R.drawable.cat),
                        null,
                        tint = if (pet.gender == "Male") maleColor else femaleColor
                    )
                    Spacer(Modifier.width(4.dp))
                    TextMaker(pet.name, 16.sp, modifier = Modifier.weight(1f, fill = false))
                }
                TextMaker(
                    pet.breed,
                    16.sp,
                    fontWeight = FontWeight.Normal
                )
                PetCardChip(pet.age)
                Row(Modifier.fillMaxWidth()) {
                    PetCardChip(pet.weight)
                    Spacer(Modifier.width(8.dp))
                    PetCardChip(pet.height)
                }
            }
        }
    }
}