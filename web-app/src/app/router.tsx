import { createBrowserRouter } from 'react-router'
import AppLayout from '@/components/layout/app-layout'
import { PrivateRoute } from './private-route'
import HomeRoute from './routes/home'
import SignInRoute from './routes/sign-in'
import ChatsRoute from './routes/chats'
import NewChatRoute from './routes/new-chat'
import ChatRoute from './routes/chat'
import DocumentsRoute from './routes/documents'

export function createRouter() {
  return createBrowserRouter([
    {
      element: <PrivateRoute />,
      children: [
        {
          element: <AppLayout />,
          children: [
            {
              path: '/',
              element: <HomeRoute />,
            },
            {
              path: '/chats',
              element: <ChatsRoute />,
            },
            {
              path: '/chats/new',
              element: <NewChatRoute />,
            },
            {
              path: '/chats/:id',
              element: <ChatRoute />,
            },
            {
              path: '/documents',
              element: <DocumentsRoute />,
            },
          ],
        },
      ],
    },
    {
      path: '/sign-in',
      element: <SignInRoute />,
    },
  ])
}
