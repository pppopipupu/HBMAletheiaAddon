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
    float speed = iTime * 1.4;
    vec2 coord = gl_FragCoord.xy * 0.08;

    float wave = sin(coord.x * 0.7 + speed) * cos(coord.y * 0.6 - speed * 0.7);
    float r = 0.6 + 0.4 * sin(coord.x * 0.5 + speed + wave);
    float g = 0.35 + 0.3 * sin(coord.y * 0.6 - speed * 0.8 + 1.5);
    float b = 0.95 + 0.05 * sin((coord.x - coord.y) * 0.4 + speed + 3.0);

    float pulse = 0.85 + 0.15 * sin(iTime * 6.0 + coord.x);

    vec2 starCoord = gl_FragCoord.xy * 0.25;
    float stars = 0.0;
    vec2 ipos = floor(starCoord);
    vec2 fpos = fract(starCoord);
    float h = hash(ipos);
    if (h > 0.93) {
        float sf = 0.5 + 0.5 * sin(iTime * 9.0 + h * 6.28);
        stars = smoothstep(0.15, 0.0, length(fpos - vec2(0.5))) * sf;
    }

    vec3 plasma = vec3(r, g, b) * pulse + vec3(stars * 0.6, stars * 0.3, stars);

    vec3 col;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec3 bg = texture2D(u_screenTexture, screenUv).rgb;
        col = mix(bg, plasma, 0.55);
    } else {
        col = plasma;
    }

    gl_FragColor = vec4(col * gl_Color.rgb, gl_Color.a);
}
