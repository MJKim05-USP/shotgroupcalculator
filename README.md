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

**주요 기능:**
* **사진 촬영 및 자동 크롭:** 촬영된 사격지 사진에서 과녁 영역을 자동으로 인식하고 잘라냅니다.
* **탄착군 분석 (Max Spread & CEP 70):** * **Max Spread:** 전체 탄착군 중 가장 멀리 떨어진 두 지점 사이의 거리를 측정합니다.
    * **CEP(70):** 통계적 기법을 통해 튀는 탄(이상치)을 제외한 사수의 실질적인 평균 탄착군(70% 정밀도)을 계산합니다.
* **자동 채점 시스템:** 입력된 과녁 지름을 기준으로 탄착 위치에 따른 점수를 실시간으로 계산합니다.

## [한국어 사용방법]
1. **사격지 준비:** 사격이 완료된 과녁지를 평평한 곳에 준비합니다.
2. **사진 촬영:** **[사진 찍기]** 버튼을 눌러 촬영합니다. (과녁이 화면 중앙에 오도록 찍어주세요.)
3. **설정 입력:** 실제 발사한 **[발수]**와 **[과녁 사이즈(cm)]**를 입력합니다. (기본값: 10발, 15cm)
4. **크기 보정:** 과녁의 중앙으로부터 드래그하여 앱상의 과녁 크기를 실제 크기와 맞춥니다.
5. **탄착 확인:** **[탄착 입력]** 버튼을 눌러 과녁 사이즈를 고정합니다. 이후 화면을 터치하여 각 탄착군 위치를 찍습니다.
6. **수정 기능:** 실수로 잘못 찍었을 경우, **[이전]** 버튼을 눌러 마지막으로 찍은 탄착을 즉시 되돌릴 수 있습니다.
