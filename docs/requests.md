# Design of Requests

This part of the documentation covers all my target designs of the module.
I'm try to complete it with learning more knowledge.

# Requests

Now I'm trying to implement an HTTP/1.1 version first,
so all discussion below will be HTTP/1.1.

For a simple GET request, we have some questions as follows:

1. Which situation we can get the response body
   1. When the status code is not `101` `102` `204` `304`
2. How can we can figure out when it's time to finish reading the response body.
   1. In the case where header `Connection` is set to `close`.
   We know it's time to finish reading when the socket is closed.
   2. In the case where if header `Connection` is set to `keep-alive`.
   if header `Transfer-Encoding` is set to chunked,we use the chunked reading strategy.
   else there will be a header named `Content-Length` which tells how many bytes we have left to read;
