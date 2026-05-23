import { createBrowserRouter } from 'react-router'
import { PrivateRoute } from './private-route'
import HomeRoute from './routes/home'
import SignInRoute from './routes/sign-in'

export function createRouter() {
  return createBrowserRouter([
    {
      element: <PrivateRoute />,
      children: [
        {
          path: '/',
          element: <HomeRoute />,
        },
      ],
    },
    {
      path: '/sign-in',
      element: <SignInRoute />,
    },
  ])
}
