package com.example.spring.rmago.service;

import com.example.spring.rmago.util.TaxiFarePolicy;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

@Slf4j
@Service
public class TaxiService {

    public double calculateReachableDistance(int fareWon) {
        LocalTime now = LocalTime.now();

        int baseFare;
        int unitFare;

        if (isLateNight(now)) {
            baseFare = TaxiFarePolicy.LATE_NIGHT_BASE_FARE;
            unitFare = TaxiFarePolicy.LATE_NIGHT_UNIT_FARE;
            log.info("심야 적용(택시 서비스)");
        } else if (isNight(now)) {
            baseFare = TaxiFarePolicy.NIGHT_BASE_FARE;
            unitFare = TaxiFarePolicy.NIGHT_UNIT_FARE;
            log.info("야간 적용(택시 서비스)");
        } else {
            baseFare = TaxiFarePolicy.DAY_BASE_FARE;
            unitFare = TaxiFarePolicy.DAY_UNIT_FARE;
            log.info("오후 적용(택시 서비스)");
        }
        log.info("🚕 받은 요금: {}, 기준요금: {}, 단위요금: {}", fareWon, baseFare, unitFare);

        double baseDistance = TaxiFarePolicy.BASE_DISTANCE;
        double unitDistance = TaxiFarePolicy.UNIT_DISTANCE;

        if (fareWon <= baseFare) {
            log.info("💰 기준요금 이하 → baseDistance 반환: {}m", baseDistance);
            return baseDistance;
        }

        int remainingFare = fareWon - baseFare;
        int extraUnits = remainingFare / unitFare;
        double extraDistance = extraUnits * unitDistance;

        double total = baseDistance + extraDistance;
        log.info("➕ 추가 거리: {}, 총 거리: {}", extraDistance, total);

        // 십 원 단위 반올림
        return Math.round(total * 10.0) / 10.0;
    }

    private boolean isNight(LocalTime time) {
        return time.isAfter(LocalTime.of(22, 0)) || time.isBefore(LocalTime.of(4, 0));
    }

    private boolean isLateNight(LocalTime time) {
        return time.isAfter(LocalTime.of(23, 0)) || time.isBefore(LocalTime.of(2, 0));
    }
}