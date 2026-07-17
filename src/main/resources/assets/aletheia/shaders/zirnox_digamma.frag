#version 120

uniform sampler2D texture;
uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;
uniform vec4 u_iconUvRange;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    vec2 uv = gl_TexCoord[0].st;

    vec4 origTexColor = vec4(0.0);
    if (uv.x >= 0.0 && uv.x <= 1.0 && uv.y >= 0.0 && uv.y <= 1.0) {
        vec2 iconGlobalUv = u_iconUvRange.xz + uv * (u_iconUvRange.yw - u_iconUvRange.xz);
        origTexColor = texture2D(texture, iconGlobalUv);
    }

    if (origTexColor.a < 0.05) {
        discard;
    }

    float speed = 1.6;
    vec2 coord = gl_FragCoord.xy * 0.06 + uv * 7.0;

    float wave = sin(coord.x * 0.7 + iTime * speed) * cos(coord.y * 0.6 - iTime * speed * 0.7);
    float r = 0.55 + 0.45 * sin(coord.x * 0.5 + iTime * speed + wave);
    float g = 0.35 + 0.35 * sin(coord.y * 0.6 - iTime * speed * 0.8 + 1.5);
    float b = 0.85 + 0.15 * sin((coord.x - coord.y) * 0.4 + iTime * speed * 1.1 + 3.0);

    float pulse = 0.85 + 0.15 * sin(iTime * 6.0 + coord.x * 1.5);

    vec2 starCoord = gl_FragCoord.xy * 0.25;
    float stars = 0.0;
    vec2 ipos = floor(starCoord);
    vec2 fpos = fract(starCoord);
    float h = hash(ipos);
    if (h > 0.93) {
        float sf = 0.5 + 0.5 * sin(iTime * 9.0 + h * 6.28);
        stars = smoothstep(0.15, 0.0, length(fpos - vec2(0.5))) * sf;
    }

    vec3 digammaColor = vec3(r, g, b) * pulse * 1.3 + vec3(stars * 0.6, stars * 0.3, stars);

    vec3 finalColor;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec3 background = texture2D(u_screenTexture, screenUv).rgb;
        finalColor = mix(background, digammaColor, origTexColor.a * 0.55);
    } else {
        finalColor = mix(origTexColor.rgb, digammaColor, 0.5);
    }

    gl_FragColor = vec4(finalColor, origTexColor.a);
}
