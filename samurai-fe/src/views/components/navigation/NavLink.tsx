import { Icon, IconProps } from "@tabler/icons-react";
import React, { MouseEventHandler, ReactNode } from "react";
import { Link, useLocation } from "react-router-dom";
import classes from "./NavLink.module.css";

interface NavLinkProps {
  link: string;
  children?: ReactNode;
  icon: React.ForwardRefExoticComponent<
    Omit<IconProps, "ref"> & React.RefAttributes<Icon>
  >;
  onClick?: MouseEventHandler<HTMLAnchorElement>;
}

export default function NavLink(props: NavLinkProps) {
  const location = useLocation();

  return (
    <Link
      className={classes.link}
      data-active={location.pathname === props.link || undefined}
      to={props.link}
      onClick={props.onClick}
    >
      <props.icon className={classes.linkIcon} stroke={1.5} />
      <span>{props.children}</span>
    </Link>
  );
}
