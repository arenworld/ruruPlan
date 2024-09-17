document.addEventListener("DOMContentLoaded", function () {
  const density = document.getElementById("density");
  const button_density = document.querySelector(".button-density");
  const button_notdensity = document.querySelector(".button-notdensity");
  const next_button = document.querySelector(".modal-submit-button");

  // 초기 작업
  next_button.style.visibility = "hidden";
  density.value = "";

  // 일정을 빡빡하게 할경우
  button_density.onclick = function () {
    density.value = "1";
    button_density.classList.add("selected");
    button_notdensity.classList.remove("selected");
    next_button.style.visibility = "visible";
  };
  // 널널하게 할경우
  button_notdensity.onclick = function () {
    density.value = "0";
    button_density.classList.remove("selected");
    button_notdensity.classList.add("selected");
    next_button.style.visibility = "visible";
  };
});
