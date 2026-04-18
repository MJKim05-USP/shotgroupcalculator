# 🎯 ShotGroupCalculator

## [English Description]
An Android application that helps shooters calculate shot groups and scores by analyzing images of targets.

## [English How to Use]
1. **Prepare Target**: Place your shot-up target on a flat surface.
2. **Capture**: Tap the **[사진 찍기]** button to take a photo. (Keep the target centered.)
3. **Set Details**: Enter the number of shots and the actual target diameter in cm (Default: 10 shots, 15cm).
4. **Calibrate**: Drag from the center to match the app's circle to the actual target size.
5. **Input Shots**: Tap **[탄착 입력]** to lock the size, then tap the screen to mark each hit point.
6. **Undo**: Use the **[이전]** button to remove the last marked shot if you make a mistake.

---

## [한국어 설명]
사격 이미지 분석을 통해 탄착군 크기와 점수를 자동으로 계산해주는 안드로이드 애플리케이션입니다.

| 기능 (Feature) | 설명 (Description) |
| :--- | :--- |
| **사진 촬영** | 사격지 촬영 기능 |
| **수동 크기 보정** | 사용자 드래그를 통한 정밀한 과녁 사이즈 매칭 |
| **Max Spread** | 전체 탄착군 중 가장 멀리 떨어진 거리 측정 |
| **CEP(70)** | 튀는 탄(이상치)을 제외한 실질적 정밀도 분석 |
| **자동 채점** | 입력된 과녁 지름 기준 실시간 점수 산출 |

* **CEP(70) ≈ 1σ (1-Sigma)** 통계적으로 정규분포의 1-시그마 범위($\approx 68.3\%$)와 매우 유사합니다.

## [한국어 사용방법]
1. **사격지 준비:** 사격이 완료된 과녁지를 평평한 곳에 준비합니다.
2. **사진 촬영:** **[사진 찍기]** 버튼을 눌러 촬영합니다. (과녁이 화면 중앙에 오도록 찍어주세요.)
3. **설정 입력:** 실제 발사한 **[발수]**와 **[과녁 사이즈(cm)]**를 입력합니다. (기본값: 10발, 15cm)
4. **크기 보정:** 과녁의 중앙으로부터 드래그하여 앱상의 과녁 크기를 실제 크기와 맞춥니다.
5. **탄착 확인:** **[탄착 입력]** 버튼을 눌러 과녁 사이즈를 고정합니다. 이후 화면을 터치하여 각 탄착군 위치를 찍습니다.
6. **수정 기능:** 실수로 잘못 찍었을 경우, **[이전]** 버튼을 눌러 마지막으로 찍은 탄착을 즉시 되돌릴 수 있습니다.

![Downloads](https://img.shields.io/github/downloads/MJKim05-USP/shotgroupcalculator/total.svg)
