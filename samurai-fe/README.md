# SamurAI Frontend

Frontend module for SamurAI - an AI-driven document search and retrieval system. Built with React, TypeScript, and Mantine UI framework.

## Project Structure

- `/src` - Source code
  - `/components` - Reusable React components
  - `/views` - Page components and layouts
  - `/model` - Data models and types
  - `/hooks` - Custom React hooks
  - `/reducers` - Redux reducers
  - `/services` - API services

## Key Features

- Modern [React](https://react.dev/) with [TypeScript](https://www.typescriptlang.org/)
- State management with [Redux Toolkit](https://github.com/reduxjs/redux-toolkit)
- UI components from [Mantine UI](https://github.com/mantinedev/mantine)
- Rich data table support with [Mantine React Table](https://github.com/KevinVandy/mantine-react-table)
- Markdown support with [marked](https://github.com/markedjs/marked) and [MDX Editor](https://github.com/mdx-editor/editor)
- Dark/Light theme support, responsive design

## Prerequisites

- Node.js 22.x
- Yarn 4.x

## Environment Setup

Create a `.env` file in the root directory with the following variables:
```env
VITE_BASE_URL=http://localhost:8080
```

## Installation

1. Install dependencies:

```bash
yarn install
```

## Development

Run the development server:

```bash
yarn dev
```

The application will be available at `http://localhost:5173`

## Building for Production

Build the application:

```bash
yarn build
```

The built files will be in the `dist` directory.
