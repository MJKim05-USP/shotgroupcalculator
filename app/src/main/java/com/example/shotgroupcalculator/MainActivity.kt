package com.example.shotgroupcalculator

import android.media.ExifInterface
import android.graphics.Matrix

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraWithPoints()
        }
    }
}

@Composable
fun CameraWithPoints() {
    var isPlacingShots by remember { mutableStateOf(false) }
    var targetDiameter by remember { mutableStateOf("15") }                   //과녁크기
    var center by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    var radius by remember { mutableStateOf(0f) }
    var isSettingTarget by remember { mutableStateOf(true) }

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var points by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
    var maxShots by remember { mutableStateOf("10") }                         //발 수
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // 임시 파일 주소(Uri) 생성 함수
    fun createImageFileUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File.createTempFile("SHOT_${timeStamp}_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // 고화질 카메라 실행 런처
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val original = BitmapFactory.decodeStream(inputStream)

                // 여기서 보정 함수를 호출하여 똑바로 세워진 비트맵을 저장합니다.
                imageBitmap = rotateImageIfRequired(original, uri, context)

                inputStream?.close()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createImageFileUri()
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val uri = createImageFileUri()
                    photoUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("사진 찍기")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = maxShots, onValueChange = { maxShots = it }, label = { Text("발수") }, modifier = Modifier.width(70.dp))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = targetDiameter, onValueChange = { targetDiameter = it }, label = { Text("과녁(cm)") }, modifier = Modifier.width(90.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = { isSettingTarget = true; isPlacingShots = false; center = null; radius = 0f; points = emptyList() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))) {
                Text("재설정")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { if (center != null) { isSettingTarget = false; isPlacingShots = true } }, enabled = center != null) {
                Text("탄착 입력")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { if (points.isNotEmpty()) points = points.dropLast(1) }, enabled = points.isNotEmpty(), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("이전")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageBitmap?.let { originalBitmap ->
            val croppedBitmap = remember(originalBitmap) {
                val size = minOf(originalBitmap.width, originalBitmap.height)
                val x = (originalBitmap.width - size) / 2
                val y = (originalBitmap.height - size) / 2
                Bitmap.createBitmap(originalBitmap, x, y, size, size)
            }

            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clipToBounds()) {
                Image(
                    bitmap = croppedBitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize().pointerInput(isSettingTarget) {
                        if (isSettingTarget) {
                            detectDragGestures(
                                onDragStart = { offset -> center = offset.x to offset.y; radius = 0f },
                                onDrag = { change, _ ->
                                    change.consume()
                                    center?.let { (cx, cy) ->
                                        val dx = change.position.x - cx
                                        val dy = change.position.y - cy
                                        radius = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                                    }
                                }
                            )
                        } else {
                            detectTapGestures { offset ->
                                val max = maxShots.toIntOrNull() ?: 5
                                if (points.size < max) points = points + (offset.x to offset.y)
                            }
                        }
                    }
                )

                Canvas(modifier = Modifier.matchParentSize()) {
                    points.forEach { drawCircle(color = Color.Red, radius = 10f, center = Offset(it.first, it.second)) }
                    center?.let {
                        drawCircle(color = Color.Green, radius = 8f, center = Offset(it.first, it.second))
                        drawCircle(color = if (isPlacingShots) Color.Green.copy(alpha = 0.3f) else Color.Green, radius = radius, center = Offset(it.first, it.second), style = Stroke(width = 3f))
                    }
                }
            }

            if (points.isNotEmpty()) {
                val pixelDiameter = radius * 2
                val actualDiameter = targetDiameter.toFloatOrNull() ?: 30f
                val scale = if (pixelDiameter > 0) actualDiameter / pixelDiameter else 0f

                // 탄착군 크기(가장 먼 두 점 사이의 거리) 계산
                var maxDistPx = 0f
                for (i in points.indices) {
                    for (j in i + 1 until points.size) {
                        val d = sqrt(((points[i].first - points[j].first) * (points[i].first - points[j].first) + (points[i].second - points[j].second) * (points[i].second - points[j].second)).toDouble()).toFloat()
                        if (d > maxDistPx) maxDistPx = d
                    }
                }

                // 총점 계산 (중심에서의 거리에 따라 0~10점)
                val totalScore = points.sumOf { p ->
                    center?.let { (cx, cy) ->
                        val d = sqrt(((p.first - cx) * (p.first - cx) + (p.second - cy) * (p.second - cy)).toDouble()).toFloat()
                        if (radius > 0) (10 - (d / (radius / 10f)).toInt()).coerceIn(0, 10) else 0
                    } ?: 0
                }

                // 1. 실 탄착군 (CEP 70) 계산을 위한 중심점(MPI) 구하기
                val avgX = points.map { it.first }.average().toFloat()
                val avgY = points.map { it.second }.average().toFloat()

                // 2. 중심점에서 각 탄착점까지의 거리 리스트 생성 후 정렬
                val distancesFromCenter = points.map { p ->
                    sqrt(((p.first - avgX) * (p.first - avgX) + (p.second - avgY) * (p.second - avgY)).toDouble()).toFloat()
                }.sorted()

                // 3. 반올림(Round) 로직으로 70% 지점의 탄 찾기
                // 예: 4발 * 0.7 = 2.8 -> 반올림하면 3 (3번째 탄 거리 사용)
                val targetIndex = (Math.round(distancesFromCenter.size * 0.7f).toInt() - 1)
                    .coerceIn(0, distancesFromCenter.size - 1)

                val cep70RadiusPx = distancesFromCenter[targetIndex]
                val cep70DiameterCm = cep70RadiusPx * 2 * scale // 반지름 x 2 x 스케일 = 실 탄착군 지름(cm)

                // 4. UI 출력 (Card 안의 Row를 사용하여 가로 배치)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween // 양 끝으로 밀어내기
                        ) {
                            Text(
                                text = "📊 탄착군: ${"%.2f".format(maxDistPx * scale)} cm",
                                color = Color.Yellow,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // 오른쪽에 CEP(70) 표시
                            Text(
                                text = "🎯 CEP(70): ${"%.2f".format(cep70DiameterCm)} cm",
                                color = Color.Green,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "🏹 총점: $totalScore / ${points.size * 10}",
                            color = Color.Cyan,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri, context: android.content.Context): Bitmap {
    val input = context.contentResolver.openInputStream(uri) ?: return bitmap
    val ei = ExifInterface(input)
    val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    input.close()

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        else -> return bitmap
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}