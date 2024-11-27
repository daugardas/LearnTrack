// Add getCookie function at the top level
const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
};

// Add fade-in animation to cards when they come into view
document.addEventListener("DOMContentLoaded", function () {
  const cards = document.querySelectorAll(".card");

  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add("fade-in");
        observer.unobserve(entry.target);
      }
    });
  });

  cards.forEach((card) => observer.observe(card));
});

// Form validation and submission handling
document.querySelectorAll("form").forEach((form) => {
  form.addEventListener("submit", function (e) {
    e.preventDefault();

    // Add animation to submit button
    const submitBtn = this.querySelector('button[type="submit"]');
    submitBtn.classList.add("animate__animated", "animate__pulse");

    // Simulate form submission
    setTimeout(() => {
      submitBtn.classList.remove("animate__animated", "animate__pulse");
      // You would typically handle the actual form submission here
    }, 1000);
  });
});

// Modal handling
const modals = document.querySelectorAll(".modal");
modals.forEach((modal) => {
  modal.addEventListener("show.bs.modal", function () {
    this.classList.add("fade-in");
  });
});

// Add to existing main.js
document.addEventListener("DOMContentLoaded", function () {
  const createCourseForm = document.getElementById("createCourseForm");
  if (createCourseForm) {
    createCourseForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const submitBtn = this.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.innerHTML =
        '<i class="fas fa-spinner fa-spin me-2"></i>Creating...';

      const formData = new FormData(this);
      const data = {};
      formData.forEach((value, key) => (data[key] = value));

      fetch(this.action, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
        },
        body: JSON.stringify(data),
        credentials: "include",
      })
        .then((response) => {
          if (response.ok) {
            window.location.reload();
          } else {
            console.log(response);
            throw new Error("Failed to create course");
          }
        })
        .catch((error) => {
          submitBtn.disabled = false;
          submitBtn.innerHTML = "Create Course";
          alert("Failed to create course: " + error.message);
        });
    });
  }
});

function editCourse(courseId, courseName, courseDescription) {
  document.getElementById("editCourseName").value = courseName;
  document.getElementById("editCourseDescription").value = courseDescription;

  const editModal = document.getElementById("editCourseModal");
  const modal = new bootstrap.Modal(editModal);
  modal.show();
}

document.addEventListener("DOMContentLoaded", function () {
  const editCourseForm = document.getElementById("editCourseForm");
  if (editCourseForm) {
    editCourseForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const submitBtn = this.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.innerHTML =
        '<i class="fas fa-spinner fa-spin me-2"></i>Updating...';

      const formData = new FormData(this);
      const data = {};
      formData.forEach((value, key) => (data[key] = value));

      fetch(this.action, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
        },
        body: JSON.stringify(data),
        credentials: "include",
      })
        .then((response) => {
          if (response.ok) {
            window.location.reload();
          } else {
            throw new Error("Failed to update course");
          }
        })
        .catch((error) => {
          submitBtn.disabled = false;
          submitBtn.innerHTML = "Update Course";
          alert("Failed to update course: " + error.message);
        });
    });
  }
});

function deleteCourse(courseId) {
  if (confirm("Are you sure you want to delete this course?")) {
    fetch(`/courses/${courseId}`, {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
      },
      credentials: "include",
    })
      .then((response) => {
        if (response.ok) {
          window.location.href = "/courses";
        } else {
          throw new Error("Failed to delete course");
        }
      })
      .catch((error) => {
        alert("Failed to delete course: " + error.message);
      });
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const createLessonForm = document.getElementById("createLessonForm");
  if (createLessonForm) {
    createLessonForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const submitBtn = this.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.innerHTML =
        '<i class="fas fa-spinner fa-spin me-2"></i>Creating...';

      const formData = new FormData(this);
      const data = {};
      formData.forEach((value, key) => (data[key] = value));

      fetch(this.action, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
        },
        body: JSON.stringify(data),
        credentials: "include",
      })
        .then((response) => {
          if (response.ok) {
            window.location.reload();
          } else {
            throw new Error("Failed to create lesson");
          }
        })
        .catch((error) => {
          submitBtn.disabled = false;
          submitBtn.innerHTML = "Create Lesson";
          alert("Failed to create lesson: " + error.message);
        });
    });
  }
});

function deleteLesson(courseId, lessonId) {
  if (confirm("Are you sure you want to delete this lesson?")) {
    fetch(`/courses/${courseId}/lessons/${lessonId}`, {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
      },
      credentials: "include",
    })
      .then((response) => {
        if (response.ok) {
          window.location.reload();
        } else {
          throw new Error("Failed to delete lesson");
        }
      })
      .catch((error) => {
        alert("Failed to delete lesson: " + error.message);
      });
  }
}

function editLesson(courseId, lessonId) {
  const lessonCard = document.querySelector(`[data-lesson-id="${lessonId}"]`);
  const lessonTitle = lessonCard.querySelector(".card-title").textContent;
  const lessonDescription = lessonCard.querySelector(".card-text").textContent;

  const editModal = document.getElementById("editLessonModal");
  const editForm = document.getElementById("editLessonForm");

  editForm.setAttribute("action", `/courses/${courseId}/lessons/${lessonId}`);
  document.getElementById("editLessonTitle").value = lessonTitle;
  document.getElementById("editLessonDescription").value = lessonDescription;

  const modal = new bootstrap.Modal(editModal);
  modal.show();
}
document.addEventListener("DOMContentLoaded", function () {
  const editLessonForm = document.getElementById("editLessonForm");
  if (editLessonForm) {
    editLessonForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const submitBtn = this.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.innerHTML =
        '<i class="fas fa-spinner fa-spin me-2"></i>Updating...';

      const formData = new FormData(this);
      const data = {};
      formData.forEach((value, key) => (data[key] = value));

      fetch(this.action, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
        },
        body: JSON.stringify(data),
        credentials: "include",
      })
        .then((response) => {
          if (response.ok) {
            window.location.reload();
          } else {
            throw new Error("Failed to update lesson");
          }
        })
        .catch((error) => {
          submitBtn.disabled = false;
          submitBtn.innerHTML = "Update Lesson";
          alert("Failed to update lesson: " + error.message);
        });
    });
  }
});

function deleteReview(courseId, lessonId, reviewId) {
  if (confirm("Are you sure you want to delete this review?")) {
    fetch(`/courses/${courseId}/lessons/${lessonId}/reviews/${reviewId}`, {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
      },
      credentials: "include",
    })
      .then((response) => {
        if (response.ok) {
          window.location.reload();
        } else {
          throw new Error("Failed to delete review");
        }
      })
      .catch((error) => {
        alert("Failed to delete review: " + error.message);
      });
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const addReviewForm = document.getElementById("addReviewForm");
  if (addReviewForm) {
    addReviewForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const submitBtn = this.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.innerHTML =
        '<i class="fas fa-spinner fa-spin me-2"></i>Submitting...';

      const formData = new FormData(this);
      const data = {};
      formData.forEach((value, key) => (data[key] = value));

      fetch(this.action, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
        },
        body: JSON.stringify(data),
        credentials: "include",
      })
        .then((response) => {
          if (response.ok) {
            window.location.reload();
          } else {
            throw new Error("Failed to submit review");
          }
        })
        .catch((error) => {
          submitBtn.disabled = false;
          submitBtn.innerHTML = "Submit Review";
          alert("Failed to submit review: " + error.message);
        });
    });
  }
});

