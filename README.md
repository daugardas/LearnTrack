# LearnTrack

LearnTrack is a simple yet powerful system designed for managing courses, lessons, and reviews. It provides a user-friendly interface for lecturers, students, and administrators to interact with educational content effectively.

## Overview

The LearnTrack platform allows users to create, manage, and review courses and lessons. It is built using modern web technologies, ensuring a responsive and engaging user experience. The application is structured to support various user roles, including lecturers, students, and administrators, each with specific permissions and functionalities.

## Functional Requirements

### Course Management

- **Lecturers** can create, edit, and delete their courses.
- **Users** can view available courses.
- **Users** can enroll in courses, becoming students.
- **Administrators** can edit and remove existing courses.

### Lesson Management

- **Lecturers** can create, edit, and delete lessons for their courses.
- **Lecturers** can add various types of content to lessons, including text and videos.
- **Administrators** can remove lessons.

### Review Management

- **Students** can create reviews for lessons.
- **Students** can delete their reviews.
- **Lecturers** can view all reviews for their lessons.
- **Users** can see reviews for lessons.
- **Users who created a review** can delete that review.

## Used Technologies

- **Front-end**: SpringBoot WebFlux, ThymeLeaf, Bootstrap, Google Fonts (Roboto), Font Awesome icons
- **Back-end**: Java (Spring boot) Authorization and Resource server implementing OAuth2
- **Database**: PostgreSQL