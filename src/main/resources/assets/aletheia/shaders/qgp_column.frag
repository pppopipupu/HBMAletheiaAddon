#version 120

uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    float chaos = iTime * 3.0;
    vec2 coord = gl_FragCoord.xy * 0.10;

    float n = hash(floor(coord + chaos));
    float turb = sin(coord.x * 2.0 + chaos) * cos(coord.y * 1.8 - chaos) + n * 2.0 - 1.0;

    float r = 0.5 + 0.5 * sin(turb * 3.0 + iTime * 5.0);
    float g = 0.9 + 0.1 * sin(turb * 2.0 + iTime * 7.0);
    float b = 1.0;

    vec3 plasma = vec3(r, g, b) * (0.8 + 0.2 * sin(iTime * 12.0 + turb * 4.0));

    vec3 col;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec2 refr = vec2(sin(iTime + turb), cos(iTime + turb)) * 0.01;
        vec3 bg = texture2D(u_screenTexture, clamp(screenUv + refr, 0.001, 0.999)).rgb;
        col = mix(bg, plasma, 0.6);
    } else {
        col = plasma;
    }

    gl_FragColor = vec4(col * gl_Color.rgb, gl_Color.a);
}
