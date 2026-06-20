import { createBrowserRouter, redirect } from 'react-router'
import AppLayout from '@/components/layout/app-layout'
import { PrivateRoute } from './private-route'
import SignInRoute from './routes/sign-in'
import SignUpRoute from './routes/sign-up'
import ChatsRoute from './routes/chats'
import ChatRoute from './routes/chat'
import ProjectsRoute from './routes/projects'
import ProjectRoute from './routes/project'

export function createRouter() {
  return createBrowserRouter([
    {
      element: <PrivateRoute />,
      children: [
        {
          element: <AppLayout />,
          children: [
            {
              index: true,
              loader: () => redirect('/projects'),
            },
            {
              path: '/projects',
              element: <ProjectsRoute />,
            },
            {
              path: '/projects/:id',
              element: <ProjectRoute />,
            },
            {
              path: '/chats',
              element: <ChatsRoute />,
            },
            {
              path: '/chats/:id',
              element: <ChatRoute />,
            },
          ],
        },
      ],
    },
    {
      path: '/sign-up',
      element: <SignUpRoute />,
    },
    {
      path: '/sign-in',
      element: <SignInRoute />,
    },
  ])
}
