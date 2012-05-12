About
-----

NeuroSca is a proof-of-concept HTTP server written in Scala. By no means is it even close for production use — it's merely an exploration of Scala syntax, features, actor model and performance.


Performance
-----------

The benchmarks were conducted on quad core i5 machine after warming up JVM.

<table>
  <thead>
    <tr>
      <th>Concurrent connections</th>
      <th>Requests per second</th>
  </thead>
  <tbody>
    <tr>
      <td>1</td>
      <td>7367.57</td>
    </tr>
    <tr>
      <td>2</td>
      <td>12089.27</td>
    </tr>
    <tr>
      <td>3</td>
      <td>14496.54</td>
    </tr>
    <tr>
      <td>4</td>
      <td>15318.63</td>
    </tr>
    <tr>
      <td>5</td>
      <td>17148.54</td>
    </tr>
    <tr>
      <td>10</td>
      <td>19702.08</td>
    </tr>
    <tr>
      <td>25</td>
      <td>19433.32</td>
    </tr>
    <tr>
      <td>50</td>
      <td>20356.65</td>
    </tr>
    <tr>
      <td>500</td>
      <td>20416.03</td>
    </tr>
    <tr>
      <td>750</td>
      <td>18552.00</td>
    </tr>
    <tr>
      <td>1000</td>
      <td>18610.54</td>
    </tr>
  </tbody>
</table>

Caveats
-------

All the things! Due to the server being totally careless when operating on sockets, it's a common issue of OS closing the application with "too many open files (ports)" message or resetting the connection. As mentioned above, this project is just an exploration of Scala.

Bonus
-----

Scala language has a fantastic feature called pattern matching. Just hit `/version` or `/multiply/3/9` - it's just like Django's urls.py but built in your language. Have a look at [another example](https://gist.github.com/2668943): a simple REPL reacting on certain commands.
