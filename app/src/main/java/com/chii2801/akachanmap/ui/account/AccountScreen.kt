package com.chii2801.akachanmap.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chii2801.akachanmap.data.model.BabyGender
import com.chii2801.akachanmap.data.model.Prefecture
import com.chii2801.akachanmap.viewmodel.AccountViewModel

private val avatarOptions = listOf(
    "👩", "👩‍🦱", "👩‍🦰", "👩‍🦳", "👩‍🦲",
    "👨", "👨‍🦱", "👨‍🦰", "👨‍🦳", "👨‍🦲",
    "🧑", "🧑‍🦱", "🧑‍🦰", "🧑‍🦳"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBack: () -> Unit,
    viewModel: AccountViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var nickname by remember(profile) { mutableStateOf(profile?.nickname ?: "") }
    var selectedGender by remember(profile) { mutableStateOf(profile?.babyGender ?: BabyGender.SECRET) }
    var birthdate by remember(profile) { mutableStateOf(profile?.babyBirthdate ?: "") }
    var selectedAvatar by remember(profile) { mutableStateOf(profile?.avatarEmoji ?: "👩") }
    var selectedPrefecture by remember(profile) { mutableStateOf(profile?.prefecture ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPrefPicker by remember { mutableStateOf(false) }
    val isFirstTime = profile?.nickname.isNullOrBlank()
    var agreedToTerms by remember { mutableStateOf(!isFirstTime) }

    if (saveSuccess) {
        LaunchedEffect(Unit) {
            viewModel.clearSaveSuccess()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("アカウント", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B6B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8F8))
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // アバター選択
            SectionLabel("アバター")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                avatarOptions.chunked(5).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { emoji ->
                            val isSelected = emoji == selectedAvatar
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        if (isSelected) Color(0xFFFFE0E0) else Color.White,
                                        CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFFF6B6B) else Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = emoji }
                            ) {
                                Text(emoji, fontSize = 28.sp)
                            }
                        }
                    }
                }
            }

            // ニックネーム
            SectionLabel("ニックネーム")
            OutlinedTextField(
                value = nickname,
                onValueChange = { if (it.length <= 20) nickname = it },
                placeholder = { Text("例：しんちゃんママ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // 赤ちゃんの性別
            SectionLabel("赤ちゃんの性別")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BabyGender.values().forEach { gender ->
                    val isSelected = gender == selectedGender
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGender = gender },
                        label = { Text(gender.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF6B6B),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 都道府県
            SectionLabel("都道府県")
            OutlinedTextField(
                value = Prefecture.values().firstOrNull { it.name == selectedPrefecture }?.label ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("タップして選択") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrefPicker = true },
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledPlaceholderColor = Color.Gray
                )
            )

            // 赤ちゃんの生年月日
            SectionLabel("赤ちゃんの生年月日")
            OutlinedTextField(
                value = birthdate,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("タップして選択") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledPlaceholderColor = Color.Gray
                )
            )

            // 初回のみ利用規約同意チェックボックス
            if (isFirstTime) {
                TermsAgreementRow(
                    agreed = agreedToTerms,
                    onToggle = { agreedToTerms = it }
                )
            }

            // 保存ボタン
            Button(
                onClick = { viewModel.saveProfile(nickname, selectedGender, birthdate, selectedAvatar, selectedPrefecture) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isSaving && (if (isFirstTime) agreedToTerms && nickname.isNotBlank() else true),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("保存する！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDatePicker) {
        BirthdatePicker(
            onConfirm = { birthdate = it; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showPrefPicker) {
        PrefecturePicker(
            onConfirm = { selectedPrefecture = it; showPrefPicker = false },
            onDismiss = { showPrefPicker = false }
        )
    }
}

@Composable
fun TermsAgreementRow(agreed: Boolean, onToggle: (Boolean) -> Unit) {
    val uriHandler = LocalUriHandler.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = agreed,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF6B6B))
        )
        val text = buildAnnotatedString {
            append("　")
            withStyle(SpanStyle(color = Color(0xFFFF6B6B), textDecoration = TextDecoration.Underline)) {
                append("利用規約")
            }
            append("・")
            withStyle(SpanStyle(color = Color(0xFFFF6B6B), textDecoration = TextDecoration.Underline)) {
                append("プライバシーポリシー")
            }
            append("に同意する")
        }
        Text(
            text = text,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onToggle(!agreed) }
        )
    }
    Row(modifier = Modifier.padding(start = 48.dp)) {
        TextButton(
            onClick = { uriHandler.openUri("https://chii2801.github.io/AkaChangMap/terms-of-service") },
            contentPadding = PaddingValues(0.dp)
        ) { Text("利用規約を読む →", fontSize = 11.sp, color = Color.Gray) }
        TextButton(
            onClick = { uriHandler.openUri("https://chii2801.github.io/AkaChangMap/privacy-policy") },
            contentPadding = PaddingValues(0.dp)
        ) { Text("プライバシーポリシーを読む →", fontSize = 11.sp, color = Color.Gray) }
    }
}

@Composable
fun PrefecturePicker(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var expandedRegion by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("都道府県を選択", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Prefecture.values()
                    .groupBy { it.region }
                    .forEach { (region, prefs) ->
                        val isExpanded = expandedRegion == region.name
                        TextButton(
                            onClick = { expandedRegion = if (isExpanded) null else region.name },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${if (isExpanded) "▼" else "▶"} ${region.label}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B6B),
                                fontSize = 14.sp
                            )
                        }
                        if (isExpanded) {
                            prefs.chunked(4).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    row.forEach { pref ->
                                        OutlinedButton(
                                            onClick = { onConfirm(pref.name) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(pref.label, fontSize = 11.sp)
                                        }
                                    }
                                    repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル", color = Color.Gray) }
        }
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
}

@Composable
fun BirthdatePicker(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val years = (currentYear downTo currentYear - 5).map { it.toString() }
    val months = (1..12).map { "%02d".format(it) }

    var selectedYear by remember { mutableStateOf(currentYear.toString()) }
    var selectedMonth by remember { mutableStateOf("01") }
    var selectedDay by remember { mutableStateOf("01") }

    val daysInMonth = remember(selectedYear, selectedMonth) {
        val cal = java.util.Calendar.getInstance()
        cal.set(selectedYear.toInt(), selectedMonth.toInt() - 1, 1)
        val max = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        (1..max).map { "%02d".format(it) }
    }

    // 月が変わって日が範囲外になったとき補正
    LaunchedEffect(daysInMonth) {
        if (selectedDay !in daysInMonth) selectedDay = daysInMonth.last()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("生年月日を選択", fontWeight = FontWeight.Bold) },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PickerDropdown(
                    label = "年",
                    options = years,
                    selected = selectedYear,
                    onSelect = { selectedYear = it },
                    modifier = Modifier.weight(2.5f)
                )
                PickerDropdown(
                    label = "月",
                    options = months,
                    selected = selectedMonth,
                    onSelect = { selectedMonth = it },
                    modifier = Modifier.weight(1.5f)
                )
                PickerDropdown(
                    label = "日",
                    options = daysInMonth,
                    selected = selectedDay,
                    onSelect = { selectedDay = it },
                    modifier = Modifier.weight(1.5f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm("$selectedYear/$selectedMonth/$selectedDay") }) {
                Text("OK", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル", color = Color.Gray) }
        }
    )
}

@Composable
private fun PickerDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selected, fontSize = 14.sp)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onSelect(option); expanded = false }
                    )
                }
            }
        }
    }
}
