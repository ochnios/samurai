import { Navigate, Outlet, useOutlet } from "react-router-dom";
import Navigation from "./Navigation.tsx";
import { Container } from "@mantine/core";
import classes from "./Layout.module.css";

export default function Layout() {
  const outlet = useOutlet();
  return (
    <Container fluid px={0} className={classes.mainContainer}>
      <Navigation />
      <Container fluid>
        {outlet == null && <Navigate to="/" />}
        <Outlet />
      </Container>
      <div className={classes.clear}></div>
    </Container>
  );
}
