import './App.css';
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./Home";
import RouteDisplay from "./components/RouteDisplay";
import TrackFriend from "./components/TrackFriend";
import Annotations from "./components/Annotations";
import LogIn from "./components/LogIn";
import SignUp from "./components/SignUp";

function App() {
  return (
      <Router>
          <Navbar />
          <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/LogIn" element={<LogIn />} />
              <Route path="/SignUp" element={<SignUp />} />
              <Route path="/RouteDisplay" element={<RouteDisplay />} />
              <Route path="/TrackFriend" element={<TrackFriend />} />
              <Route path="/Annotations" element={<Annotations />} />
          </Routes>
      </Router>
  );
}

export default App;
