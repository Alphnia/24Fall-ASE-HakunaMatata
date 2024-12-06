const options = {
  enableHighAccuracy: true,
  timeout: 5000,
  maximumAge: 0,
};

function success(pos) {
  const crd = pos.coords;

  console.log("Your current position is:");
  console.log(`Latitude : ${crd.latitude}`);
  console.log(`Longitude: ${crd.longitude}`);
  console.log(`More or less ${crd.accuracy} meters.`);
}

function error(err) {
  console.warn(`ERROR(${err.code}): ${err.message}`);
}

let watchId;
let intervalId;

function fetchPosition() {
  navigator.geolocation.getCurrentPosition(
    (position) => {
      console.log("Position updated:", position.coords);
      fetch
    },
    (error) => {
      console.error("Error fetching position:", error);
    },
    options
  );
}

export const startShareLocation = () => {
  if (navigator.geolocation) {
    // watchId = navigator.geolocation.watchPosition(success, error, options);
    intervalId = setInterval(fetchPosition, 5000);
    console.log("Started watching position.");
  } else {
    console.error("Geolocation is not supported by your browser.");
  }
};

export const stopShareLocation = () => {
  if (watchId !== undefined) {
    navigator.geolocation.clearWatch(watchId);
    console.log("Stopped watching position.");
  } else if (intervalId !== undefined) {
    clearInterval(intervalId);
    intervalId = null; // Reset the interval ID
    console.log("Stopped sharing location.");
  } else {
    console.error("No position tracking to stop.");
  }
};
