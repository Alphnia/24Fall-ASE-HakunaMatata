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

export const startShareLocation = () => {
  if (navigator.geolocation) {
    watchId = navigator.geolocation.watchPosition(success, error, options);
    console.log("Started watching position.");
  } else {
    console.error("Geolocation is not supported by your browser.");
  }
};

export const stopShareLocation = () => {
  if (watchId !== undefined) {
    navigator.geolocation.clearWatch(watchId);
    console.log("Stopped watching position.");
  } else {
    console.error("No position tracking to stop.");
  }
};
