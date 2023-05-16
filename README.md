# 펫 시터와 산책 메이트 매칭 서비스

## 개요
- 1인 가구의 증가와 함께 반려동물 시장 규모가 확장되고 있음. 이러한 트렌드를 바탕으로 펫 시터 및 산책 메이트를 매칭해주는 반려동물 서비스를 개발
- 이 프로젝트를 통해 반려동물 보호자들의 라이프스타일에 최적화된 서비스를 제공하고, 반려동물 산업의 성장에 기여하고자 함
- 기술적인 목표는 아래와 같음
  - 안정성과 품질 향상을 위해 테스트 커버리지 70% 
  - 이슈 발생 시, 원인 파악 및 문제 해결을 통한 서비스 안정성 확보
  - 지속적인 성능 개선을 통해 궁극적으로 대규모 트래픽에도 견고한 애플리케이션 구현
<br>

## Technical Issues
- [분산 서버 환경에서 동시성 문제 발생](https://github.com/iamjunhyeok/pet-sitter-and-walk-mate/wiki/%EB%B6%84%EC%82%B0-%EC%84%9C%EB%B2%84-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%EB%B0%9C%EC%83%9D)

- [Redis 를 활용한 인증번호 관리](https://github.com/iamjunhyeok/pet-sitter-and-walk-mate/wiki/%08Redis-%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D%EB%B2%88%ED%98%B8-%EA%B4%80%EB%A6%AC)

- [이메일 유효성 검사 전, @Email 검증부터](https://github.com/iamjunhyeok/pet-sitter-and-walk-mate/wiki/%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9C%A0%ED%9A%A8%EC%84%B1-%EA%B2%80%EC%82%AC-%EC%A0%84,-@Email-%EA%B2%80%EC%A6%9D%EB%B6%80%ED%84%B0)

- [일대일 관계에서 지연 로딩이 작동하지 않는 문제](https://github.com/iamjunhyeok/pet-sitter-and-walk-mate/wiki/%EC%9D%BC%EB%8C%80%EC%9D%BC-%EA%B4%80%EA%B3%84%EC%97%90%EC%84%9C-%EC%A7%80%EC%97%B0-%EB%A1%9C%EB%94%A9%EC%9D%B4-%EC%9E%91%EB%8F%99%ED%95%98%EC%A7%80-%EC%95%8A%EB%8A%94-%EB%AC%B8%EC%A0%9C)

- [자동화를 위한 CI/CD Pipeline 구축](https://github.com/iamjunhyeok/pet-sitter-and-walk-mate/wiki/%EC%9E%90%EB%8F%99%ED%99%94%EB%A5%BC-%EC%9C%84%ED%95%9C-CI-CD-Pipeline-%EA%B5%AC%EC%B6%95)

- [지속적인 성능 개선을 위한 정적 코드 분석 도구](https://github.com/iamjunhyeok/pet-sitter-and-walk-mate/wiki/%EC%A7%80%EC%86%8D%EC%A0%81%EC%9D%B8-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0%EC%9D%84-%EC%9C%84%ED%95%9C-%EC%A0%95%EC%A0%81-%EC%BD%94%EB%93%9C-%EB%B6%84%EC%84%9D-%EB%8F%84%EA%B5%AC)
<br>

## 사용 기술 및 환경
- Java
- Spring Boot
- JPA/Hibernate
- MySQL
- Git
- Gradle
- Redis
- Docker
- Jenkins
- AWS (EC2, ECR/ECS, RDS, S3/CloudFront)
<br>

## 아키텍처
![image](https://user-images.githubusercontent.com/93698160/227998673-4b7a8ec4-db8a-45ad-8c38-d45ba0f188d6.png)
<br>
<br>

## 프로토타입
###### 프로토타입 링크 : https://ovenapp.io/view/fY9mrvewCVOoRoRI6YmHf0da1DKxhT9f/VpWD5

![image](https://user-images.githubusercontent.com/93698160/227999016-646e755c-0683-421f-b517-cee13d264eec.png)
<br>
<br>

## ERD
![pet-sitter-and-walk-mate (7)](https://user-images.githubusercontent.com/93698160/229858339-f6400540-aa4b-436a-aa0e-53ca1a72c7d8.png)
